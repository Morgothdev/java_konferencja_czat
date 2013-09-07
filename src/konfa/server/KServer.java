package konfa.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

import konfa.Consts;
import konfa.communication.protocol.MessagesQueue;
import konfa.server.service.ServiceRequestThread;

/**
 * Apliakcja serwera, inicjalizuje potrzebne obiekty - kolejkę wiadomości,
 * menedżera połączeń, uruchamia wątek nasłuchujący na polecenia z apliakcji
 * Service i ogólnie startuje serwer.
 * 
 * @author mateusz
 * 
 */
public class KServer extends Thread {

	private final ConnectionsManager connectionsManager;
	private final MessagesManager messagesManager;
	private final MessagesQueue<SrvMessage> queue;
	private final MyLogger log = MyLogger.getLogger();
	private final Sender sender;
	private ServiceRequestThread serviceThread;
	private String state = "off";

	@SuppressWarnings("javadoc")
	public KServer() {
		state = "Starting...";
		setUncaughtExceptionHandler(log);
		connectionsManager = new ConnectionsManager();
		queue = new MessagesQueue<SrvMessage>();
		messagesManager = MessagesManager.getManager();
		messagesManager.setMessageQueue(queue);
		sender = new Sender(connectionsManager, queue);
		sender.start();
		try {
			serviceThread = new ServiceRequestThread(
					Consts.portToListenOnService, this);
			serviceThread.start();
		} catch (IOException e) {
			log.log(Level.SEVERE, "", e);
		}
		state = "Running";
	}

	@Override
	public void run() {
		log.info("KSever starts");
		// SSLServerSocketFactory factory = (SSLServerSocketFactory)
		// SSLServerSocketFactory
		// .getDefault();

		try {
			// SSLServerSocket serverSocket = (SSLServerSocket) factory
			// .createServerSocket(Consts.portToListenOnClientsConnections);
			ServerSocket serverSocket = new ServerSocket(
					Consts.portToListenOnClientsConnections);
			serverSocket.setSoTimeout(2000);
			try {
				while (!interrupted()) {
					try {
						Socket socket;
						try {
							socket = serverSocket.accept();
						} catch (SocketTimeoutException e) {
							continue;
						}
						log.info("server RECEIVED new connection with socket: "
								+ socket.toString() + "on port "
								+ socket.getLocalPort());
						Connection con = new Connection(socket, queue);
						connectionsManager.addConnection(con);
						log.info("server accepts connection");
					} catch (IOException e) {
						log.log(Level.SEVERE, "", e);
					}
				}
			} finally {
				serverSocket.close();
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "", e);
		}
		log.info("listenieng new connections thread is out.");
	}

	/**
	 * Wyłącza serwer, wyłącza wszystkie wątki i sprząta po sobie.
	 */
	public void turnOff() {
		state = "Shutting off...";
		log.config("SERVICE REQUEST TO POWER OFF");
		interrupt(); // OK
		sender.interrupt(); // OK
		connectionsManager.closeConnections();
	}

	/**
	 * Drukuje statystyki do strumienia
	 * 
	 * @param out
	 *            - strumień do którego ma być zapis
	 */
	public void printStats(PrintWriter out) {
		log.info("SERVICE REQUEST ME TO PRINT MY STATS");
	}

	/**
	 * @return stan serwera
	 */
	public String getServerState() {
		return state;
	}

}
