package konfa.server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Klasa zarządzająca połączeniami z klientami. Leniwie sprawdza, czy połączenia
 * są zalogowane i aktywne podczas zapytania o aktualną listę metodą
 * getConnections
 * 
 * @author mateusz
 */
public class ConnectionsManager {

	private final List<Connection> connections = new LinkedList<Connection>();
	private final List<Connection> unlogged = new LinkedList<Connection>();
	private static final MyLogger log = MyLogger.getLogger();

	/**
	 * @return zalogowane połączenia, z którymi nie było na razie kłopotów
	 */
	public List<Connection> getConnections() {
		Iterator<Connection> i = connections.iterator();
		Connection con;
		// wyrzucenie z listy połączeń tych z utraconym połączeniem
		while (i.hasNext()) {
			con = i.next();
			if (!con.isConnected()) {
				i.remove();
				log.info("connection is closed, remove from list");
			}
		}
		// przeniesienie zalogowanych już użytkowników do listy aktywnych
		// połączeń
		i = unlogged.iterator();
		while (i.hasNext()) {
			con = i.next();
			if (con.isLogged()) {
				i.remove();
				connections.add(con);
			}
		}
		return connections;
	}

	/**
	 * Dodaje połączenie do menedżera, ten sobie o razu sprawdza, czy jest
	 * zalogowane i dodaje do odpowedniej listy
	 * 
	 * @param c
	 *            - połączenie do dodania
	 */
	public void addConnection(Connection c) {
		if (c.isLogged()) {
			connections.add(c);
			log.info("connection added to connections");
		} else {
			unlogged.add(c);
			log.info("connection added to unlogged connections");
		}
	}

	/**
	 * @param con
	 *            - połączenie do usunięcia z menedżera
	 */
	public void removeConnection(Connection con) {
		if (!connections.remove(con)) {
			unlogged.remove(con);
		}
	}

	/**
	 * Zamyka wszystkie połączenia, sluży jako procedura sprzątająca przed
	 * wyłączeniem serwera
	 */
	public void closeConnections() {
		for (Connection con : connections) {
			con.close();
		}
		for (Connection con : unlogged) {
			con.close();
		}
		connections.clear();
	}

}
