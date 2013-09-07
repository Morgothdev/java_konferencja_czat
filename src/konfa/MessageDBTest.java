package konfa;

import java.util.Iterator;
import java.util.List;

import konfa.server.SrvMessage;
import konfa.server.User;
import konfa.server.Users;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@SuppressWarnings("javadoc")
public class MessageDBTest {
	private static SessionFactory factory;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		System.out.println("start");
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		MessageDBTest mt = new MessageDBTest();
		// for (User u : Users.getDefaultUsers()) {
		// u.fireOnChangeListener();
		// }

		/* Add few employee records in database */
		// Serializable empID1 = mt.addMessage("Mateuszjaje", "jaje@gmail.com",
		// "mjaj");
		// Serializable empID2 = mt.addMessage("Mateuszj", "jaje@gmail.com",
		// "mjaj");
		// System.out.println(empID1.toString());
		// System.out.println(empID2.toString());
		/* List down all the employees */
		mt.listUsers();
		try {
			zalegle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// /* Update employee's records */
		// mt.updateEmployee(empID1, 5000);
		//
		// /* Delete an employee from the database */
		// mt.deleteEmployee(empID2);
		//
		// /* List down new list of the employees */
		// mt.listEmployees();
		System.out.println("klniec");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void zalegle() throws Exception {
		User user = Users.getUserFromMail("rutka@gmail.com");
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List query = session
					.createQuery("from SrvMessage where id>:user_last")
					.setParameter("user_last", user.getLastMessageID()).list();
			Iterator<SrvMessage> i = query.iterator();
			while (i.hasNext()) {
				System.out.println(i.next());
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

	/* Method to CREATE an employee in the database */
	// public Serializable addMessage(User usr) {
	// Session session = factory.openSession();
	// Transaction tx = null;
	// Serializable employeeID = null;
	// try {
	// tx = session.beginTransaction();
	// User employee = new User(usr.getDisplayName(), usr.getMail(),
	// usr.getPassword());
	// employeeID = session.save(employee);
	// tx.commit();
	// } catch (HibernateException e) {
	// if (tx != null)
	// tx.rollback();
	// e.printStackTrace();
	// } finally {
	// session.close();
	// }
	// return employeeID;
	// }
	//
	/* Method to READ all the employees */
	@SuppressWarnings({ "rawtypes" })
	public void listUsers() {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List users = session.createQuery("from User").list();
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				System.out.print("NAME: " + user.getDisplayName());
				System.out.print(" MAIL: " + user.getMail());
				System.out.println(" PASSWORD: " + user.getPassword());
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	// /* Method to UPDATE salary for an employee */
	// public void updateEmployee(Integer EmployeeID, int salary) {
	// Session session = factory.openSession();
	// Transaction tx = null;
	// try {
	// tx = session.beginTransaction();
	// Employee employee = (Employee) session.get(Employee.class,
	// EmployeeID);
	// employee.setSalary(salary);
	// session.update(employee);
	// tx.commit();
	// } catch (HibernateException e) {
	// if (tx != null)
	// tx.rollback();
	// e.printStackTrace();
	// } finally {
	// session.close();
	// }
	// }
	//
	// /* Method to DELETE an employee from the records */
	// public void deleteEmployee(Integer EmployeeID) {
	// Session session = factory.openSession();
	// Transaction tx = null;
	// try {
	// tx = session.beginTransaction();
	// Employee employee = (Employee) session.get(Employee.class,
	// EmployeeID);
	// session.delete(employee);
	// tx.commit();
	// } catch (HibernateException e) {
	// if (tx != null)
	// tx.rollback();
	// e.printStackTrace();
	// } finally {
	// session.close();
	// }
	// }

}