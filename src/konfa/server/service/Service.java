package konfa.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import konfa.Consts;

/**
 * Aplikacja konsolowa do zarządzania serwerem, komunikuje się z nim przez
 * socketa, na którym serwer nasłuchuje na polecenia i je wykonuje. Potrafi: -
 * rozkazać serwerowi się wyłączyć - podpiąć się jako nasłuchujący na logi
 * 
 * @author mateusz
 */
public class Service {

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws UnknownHostException,
			IOException, ClassNotFoundException {

		if (args[0].equals("stop")) {
			runStop();
		} else if (args[0].equals("stat")) {
			runStats();
		} else if (args[0].equals("logs")) {
			runLogs();
		} else if (args[0].equals("start")) {
			runServer();
		} else if (args[0].equals("state")) {
			runGetState();
		} else if (args[0].equals("defaultUsers")) {
			runDefaultUsers();
		} else if (false) {
		} else {
			System.out.println("unrecognized option");
		}

	}

	private static void runDefaultUsers() throws UnknownHostException,
			IOException {
		Socket sock = new Socket("localhost", Consts.portToListenOnService);
		PrintWriter out;
		try {
			out = new PrintWriter(sock.getOutputStream());
			out.println("defaultUsers");
			System.out.println(new BufferedReader(new InputStreamReader(sock
					.getInputStream())).readLine());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sock.close();
		}

	}

	private static void runGetState() throws UnknownHostException, IOException {
		Socket sock = new Socket("localhost", Consts.portToListenOnService);
		PrintWriter out;
		try {
			out = new PrintWriter(sock.getOutputStream());
			out.println("state");
			System.out.println(new BufferedReader(new InputStreamReader(sock
					.getInputStream())).readLine());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sock.close();
		}
	}

	private static void runServer() {
		try {
			Runtime.getRuntime().exec("java -jar Server.jar &");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out
				.println("Server started.... it may takes few minutes to be ready :p");
	}

	private static void runLogs() throws ClassNotFoundException {
		Socket socket = null;
		try {
			socket = new Socket("localhost", Consts.portToListenOnLogsClients);
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException from creating socket");
			e.printStackTrace();
		}
		ObjectInputStream reader = null;
		try {
			reader = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("IOException from OOStream");
			e.printStackTrace();
		}

		LogRecord readed;
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		try {
			while ((readed = (LogRecord) reader.readObject()) != null) {
				handler.publish(readed);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static void runStats() throws UnknownHostException, IOException {
		Socket sock = new Socket("localhost", Consts.portToListenOnService);
		PrintWriter out;
		try {
			out = new PrintWriter(sock.getOutputStream());
			out.println("stat");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sock.close();
		}
	}

	private static void runStop() throws UnknownHostException, IOException {
		Socket sock = new Socket("localhost", Consts.portToListenOnService);
		PrintWriter out = new PrintWriter(sock.getOutputStream());
		out.println("stop");
		out.close();
		sock.close();
		System.out.println("Server is asked to stop, it take him no more "
				+ "than 10 seconds...");
	}
}
