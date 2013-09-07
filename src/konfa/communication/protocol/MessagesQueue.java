package konfa.communication.protocol;

import java.util.LinkedList;
import java.util.Queue;

import konfa.server.MyLogger;

/**
 * Synchronizowana parametryzowana kolejka, nic specjalnego
 * 
 * @author mateusz
 * 
 * @param <T>
 */
public class MessagesQueue<T> {

	private final Queue<T> messagesList;
	@SuppressWarnings("unused")
	private final MyLogger log = MyLogger.getLogger();

	@SuppressWarnings("javadoc")
	public MessagesQueue() {
		messagesList = new LinkedList<T>();
	}

	@SuppressWarnings("javadoc")
	public void add(T result) {
		synchronized (messagesList) {
			messagesList.offer(result);
			messagesList.notify();
			// log.info("message from " + result.getUserName() + " content "
			// + result.getContent() + " is added to Queue");
		}
	}

	@SuppressWarnings("javadoc")
	public T get() throws InterruptedException {
		synchronized (messagesList) {
			while (messagesList.isEmpty()) {
				messagesList.wait();
			}
			T ret = messagesList.poll();
			// log.info("message from " + ret.getUserName() + " content "
			// + ret.getContent() + " is retrieved from Queue");
			return ret;
		}
	}

	/**
	 * Zatrzymuje wątek wykonujący tą procedurę do czasu, az kolejka będzie
	 * pusta
	 * 
	 * @throws InterruptedException
	 */
	public void waitForEmpty() throws InterruptedException {
		synchronized (messagesList) {
			while (!messagesList.isEmpty()) {
				messagesList.wait();
			}
		}
	}
}
