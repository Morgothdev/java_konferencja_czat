package konfa.server;

import java.util.logging.Level;

import konfa.communication.protocol.MessagesQueue;

/**
 * Wątek rozsyłający wiadomości po zalogowanych połaczeniach, pobiera wiadomość
 * z kolejki wiadomości oraz bierze listę połączeń od menedżera połaczeń i po
 * kolei rozsyła wiadomość po połączeniach, w razie kłopotów z wysłaniem,
 * najczęściej klient się rozłączył, ustawia flage połaczenia, jako rozłączone.
 * 
 * @author mateusz
 */
public class Sender extends Thread {

	private static final MyLogger log = MyLogger.getLogger();
	private final ConnectionsManager connectionsManager;
	private final MessagesQueue<SrvMessage> messagesQueue;

	/**
	 * Tworzy obiekt sendera z koniecznymi obiektami - menedżerem połaczeń, oraz
	 * kolejką wiadomości oczekujących na wysłanie
	 * 
	 * @param connectionsManager
	 *            - menedżer połączeń
	 * @param queue
	 *            - kolejka wiadomości
	 */
	public Sender(ConnectionsManager connectionsManager,
			MessagesQueue<SrvMessage> queue) {
		this.connectionsManager = connectionsManager;
		messagesQueue = queue;
		setUncaughtExceptionHandler(log);
	}

	@Override
	public void run() {
		SrvMessage msg;
		log.info("sender starts");
		try {
			while (!interrupted()) {
				msg = messagesQueue.get();
				// TODO tu chyba najlepiej będize zapisywać wiadomość do bazy
				for (Connection con : connectionsManager.getConnections()) {
					if (con.isConnected()) {
						log.info("send message " + msg.getContent()
								+ " to user " + con.getUser().getDisplayName());
						con.write(msg);
						// log.info(new StringBuilder("connection to user ")
						// .append(con.getUser().getDisplayName())
						// .append(" is broken, catched exception: ")
						// .append(e.toString()).toString());
					}
				}
			}
		} catch (InterruptedException e) {
			log.log(Level.INFO,
					"connection's listener break by interruptException");
		}
		log.info("sender stopt.");
	}
}
