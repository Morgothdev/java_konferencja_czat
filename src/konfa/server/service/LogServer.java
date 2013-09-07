package konfa.server.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import konfa.Consts;
import konfa.server.MyLogger;

/**
 * LogSerwer jest jednocześnie serwerem jak i handlerem logów, wydał mi się
 * przydatny do czytania logów serwera, działa mniej więcej tak, że dziedziczy
 * po Handlerze, więc mozna go używac wszędze tam, gdzie uzywa się handlera i
 * jednocześnie jest serwerem nasłuchującym na połączenia, którym wysyła
 * odebrane logi.
 */
public class LogServer extends Handler {

	private final ServerSocket serverSocket;
	private final List<ObjectOutputStream> writers;
	private final Queue<LogRecord> waitingLogs;
	private final MyLogger log = MyLogger.getLogger();
	private final Thread listener;
	private final Thread sender;

	/**
	 * @param listenPort
	 *            - port na którym serwerowa część handlera nasłuchuje na nowe
	 *            podpięcia do odbierania logów
	 * @throws IOException
	 */
	public LogServer(int listenPort) throws IOException {
		super();

		serverSocket = new ServerSocket(listenPort);
		serverSocket.setSoTimeout(Consts.timoutToNonBlockListenOnConnection);
		writers = new LinkedList<ObjectOutputStream>();
		waitingLogs = new LinkedList<LogRecord>();
		setLevel(Level.ALL);

		// new connection listener
		listener = new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket;
				while (!Thread.interrupted()) {
					try {
						try {
							socket = serverSocket.accept();
						} catch (SocketTimeoutException e) {
							continue;
						}
						ObjectOutputStream writer = new ObjectOutputStream(
								socket.getOutputStream());
						synchronized (writers) {
							writers.add(writer);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.info("listerner thread from LOG SERVER stopped");

			}
		});
		listener.setUncaughtExceptionHandler(log);
		listener.start();

		// sender
		sender = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!Thread.interrupted()) {
						LogRecord record;
						synchronized (waitingLogs) {
							while (waitingLogs.isEmpty()) {
								waitingLogs.wait();
							}
							record = waitingLogs.poll();
						}
						send(record);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.info("sender thread from LOG SERVER stopped");
			}
		});
		sender.setUncaughtExceptionHandler(log);
		sender.start();

	}

	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record))
			return;

		synchronized (waitingLogs) {
			waitingLogs.offer(record);
			waitingLogs.notifyAll();
		}
	}

	private void send(LogRecord record) {
		synchronized (writers) {// Output the formatted data to the file
			for (ObjectOutputStream w : writers) {
				try {
					w.writeObject(record);
				} catch (Exception e) {
					writers.remove(w);
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void flush() {
		for (ObjectOutputStream w : writers) {
			try {
				w.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws SecurityException {
		sender.interrupt();
		listener.interrupt();
	}
}