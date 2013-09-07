package konfa.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Klasa zarządająca użytkownikami real-time, jako jedyna komunikuje się z
 * tabelą USers w bazie danych Cachuje Użytkowników pobranych z bazy, żeby user
 * był niejako singletonem
 * 
 * @author mateusz
 */
public class Users implements OnUserChangeListener {

	private final SessionFactory factory;
	private static Users instance = null;
	private static Map<String, User> returnedUsers = new HashMap<String, User>();

	private Users() {
		this.factory = HibernateUtil.getSessionFactory();
	}

	private static Users getInstance() {
		if (instance == null) {
			instance = new Users();
		}
		return instance;
	}

	/**
	 * Zwraca użytkownika z podanego maila, mail jest identyfikatorem
	 * 
	 * @param mail
	 * @return obiekt USera albo null, jeśli takiego nie ma w bazie
	 */
	public static User getUserFromMail(String mail) {
		return Users.getInstance().getUser(mail);
	}

	@Override
	public void onChange(User user) throws Exception {
		saveUser(user);
	}

	private void saveUser(User user) throws Exception {
		final Session session = this.factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(user);
			tx.commit();
		} catch (final Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			// TODO ogarnąć co jesli powtórzy się mail, poleci tu wyjątek i
			// chyba będize trzeba go wyrzucić wyżej
			throw e;
		} finally {
			session.close();
		}

	}

	private User getUser(String mail) {
		User user = null;
		final Session session = this.factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			final List<User> result = session.createQuery(
					"FROM User U WHERE U.mail = '" + mail + "'").list();

			if (result.size() == 1) {
				// jest ok
				user = result.get(0);
				user.setOnChangeListener(this);
			} else {
				// zły mail
			}
			tx.commit();
		} catch (final Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return user;
	}

	@SuppressWarnings("javadoc")
	public static List<User> getDefaultUsers() {
		final List<User> users = new LinkedList<User>();
		final Users callback = new Users();
		users.add(new User("Michał Adamczyk", "adamczyk@gmail.com", "mada",
				callback));
		users.add(new User("Mateusz Kłoczko", "kloczko@gmail.com", "mklo",
				callback));
		users.add(new User("Mateusz Jaje", "jaje@gmail.com", "mjaj", callback));
		users.add(new User("Ania Rutka", "rutka@gmail.com", "arut", callback));
		users.add(new User("Maciek Sipko", "sipko@gmail.com", "msip", callback));
		users.add(new User("Piotrek Podolski", "podolski@gmail.com", "ppod",
				callback));
		users.add(new User("Dawid Czech", "czech@gmail.com", "dcze", callback));
		users.add(new User("Daniel Bryła", "bryla@gmail.com", "dbry", callback));
		users.add(new User("Grzesiek Soliński", "solinski@gmail.com", "gsol",
				callback));
		users.add(new User("Kuba Łasisz", "lasisz@gmail.com", "jlas", callback));
		users.add(new User("testowy", "ew", "q", callback));
		return users;
	}
}
