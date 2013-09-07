package konfa.server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

import konfa.communication.protocol.IMessageListener;
import konfa.communication.protocol.MessagesQueue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @author mateusz
 * 
 */
public class MessagesManager implements IMessageListener<SrvMessage> {

	private static MessagesManager instance;

	/**
	 * @return singletonowa instancja menedżera
	 */
	public static MessagesManager getManager() {
		if (instance == null) {
			instance = new MessagesManager();
		}
		return instance;
	}

	private MessagesQueue<SrvMessage> queue;
	private final SessionFactory factory = HibernateUtil.getSessionFactory();

	@Override
	public void onNewMessage(SrvMessage msg) {
		// zapis do pasy i uzupełnienie wiadomości o pole ID z bazy
		// wsadzenie do kolejki
		synchronized (this) {
			try {
				saveMessage(msg);
			} catch (Exception e) {
				MyLogger.getLogger().log(Level.WARNING,
						"błąd zapisu wiadomości do bazy", e);
			}
			queue.add(msg);
		}
	}

	/**
	 * @param queue
	 * @return this
	 */
	public MessagesManager setMessageQueue(MessagesQueue<SrvMessage> queue) {
		this.queue = queue;
		return this;
	}

	/**
	 * @param msg
	 * @throws Exception
	 */
	public void saveMessage(SrvMessage msg) throws Exception {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Long id = (Long) session.save(msg);
			if (!id.equals(msg.getID())) {
				MyLogger.getLogger().info("dziwne!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
	}

	/**
	 * @param user
	 * @return kolejkę zalegających widomości dla danego usera
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Queue<SrvMessage> getOutstandingMessages(User user) throws Exception {
		Queue<SrvMessage> result = new LinkedList<SrvMessage>();
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List query = session
					.createQuery("from SrvMessage where id>:user_last")
					.setParameter("user_last", user.getLastMessageID()).list();
			Iterator<SrvMessage> i = query.iterator();
			while (i.hasNext()) {
				result.offer(i.next());
			}
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
		return result;
	}

}
