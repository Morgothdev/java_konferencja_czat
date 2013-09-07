package konfa.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

import konfa.Consts;
import konfa.server.KServer;
import konfa.server.MyLogger;
import konfa.server.User;
import konfa.server.Users;

/**
 * Obiekt, który pośredniczy między serwerem a aplikacją Service w wymianie
 * poleceń, robi to tak, że w wątku czeka na nowe połączenie na porcie do
 * zarządzania serwerem, odczytuje polecenie z zaakceptowanego połączenia i je
 * wykonuje
 * 
 * @author mateusz
 * 
 */
public class ServiceRequestThread extends Thread {

	private final ServerSocket socket;
	private final KServer server;
	private static final MyLogger log = MyLogger.getLogger();

	/**
	 * @param portToListen
	 *            - port na którym nasłuchuje na nowe połaczenia
	 * @param server
	 *            - serwer do którego ma kierować polecenia
	 * @throws IOException
	 *             - rzucany z konstruktora ServerSocketa
	 */
	public ServiceRequestThread(int portToListen, KServer server)
			throws IOException {
		socket = new ServerSocket(portToListen);
		socket.setSoTimeout(Consts.timoutToNonBlockListenOnConnection);
		this.server = server;
		setUncaughtExceptionHandler(log);
	}

	@Override
	public void run() {
		log.info("Service request thread started.");
		while (!interrupted()) {
			try {
				Socket serviceSocket;
				try {
					serviceSocket = socket.accept();
				} catch (SocketTimeoutException e) {
					continue;
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(
						serviceSocket.getInputStream()));
				String request = in.readLine();
				if (request.equals("stop")) {
					server.turnOff();
					interrupt();
					log.turnOff();
				} else if (request.equals("stat")) {
					PrintWriter writer;
					server.printStats(writer = new PrintWriter(serviceSocket
							.getOutputStream()));
					writer.flush();
					writer.close();
				} else if (request.equals("defaultUsers")) {
					for (User u : Users.getDefaultUsers())
						u.fireOnChangeListener();
				}
				in.close();
			} catch (IOException e) {
				log.log(Level.WARNING, "", e);
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
		log.info("Service request thread stopped.");
	}
}
