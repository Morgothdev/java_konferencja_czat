package konfa.communication.protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

import konfa.Consts;
import konfa.message.IMessage;
import konfa.server.MyLogger;

/**
 * Klasa wyjeta do zajmowania sie wylacznie polaczeniem klient-serwer w obie
 * strony klasy dziedziczace specjalizowane w jednokierunkowym polaczeniu, to to
 * co nadaje sie do uzycia w obie strony
 * 
 * @author mateusz
 */
public class Protocol {

	public static final String incorrectPassword = "`1";
	public static final String incorrectUserMail = "`2";
	public static final String correctLogin = "`3";
	public static final String adresArleadyInUSe = "`4";

	private IMessageListener<IMessage> onNewMessageListener;
	private final Thread sender;
	private final Thread listener;
	private final ObjectInputStream input;
	private final ObjectOutputStream output;
	private final MyLogger log = MyLogger.getLogger();
	protected final MessagesQueue<IMessage> queue = new MessagesQueue<IMessage>();
	private boolean connected;
	private final Socket socket;
	private ISendInfoListener sendInfoListener;

	public void setSendInfoListener(ISendInfoListener sendInfoListener) {
		this.sendInfoListener = sendInfoListener;
	}

	/**
	 * inicjalizuje polaczenie, robi streamy w obie strony, uruchamia watki,
	 * ktore obsluguja oba streamy, klasyczny sender i listener
	 * 
	 * @param connectedSocket
	 * @param messageListener
	 * @param listener
	 * @throws IOException
	 */
	public Protocol(Socket connectedSocket,
			IMessageListener<IMessage> messageListener) throws IOException {

		socket = connectedSocket;
		onNewMessageListener = messageListener;
		socket.setSoTimeout(Consts.timoutToNonBlockListenOnConnection);
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		sender = new Thread() {
			@Override
			public void run() {
				IMessage msg = null;
				log.info("sender starts");
				setUncaughtExceptionHandler(log);
				try {
					while (!interrupted()) {
						msg = queue.get();
						output.writeObject(msg);
						log.info("sending: " + msg.toString());
						if (sendInfoListener != null) {
							sendInfoListener.onSucces(msg);
						}
					}
				} catch (final InterruptedException e) {
					log.log(Level.INFO,
							"connection's listener break by exception");
				} catch (final IOException e) {
					log.log(Level.WARNING, "exception from sending Message", e);
					connected = false;
					if (sendInfoListener != null) {
						sendInfoListener.onError(msg);
					}
				}
				log.info("sender stopt.");
			}
		};

		input = new ObjectInputStream(socket.getInputStream());
		listener = new Thread() {
			@Override
			public void run() {
				log.info("listener thread " + toString() + " is started");
				setUncaughtExceptionHandler(log);
				IMessage readed;
				try {
					while (!interrupted()) {
						try {
							readed = (IMessage) input.readObject();
						} catch (final SocketTimeoutException e) {
							continue;
						}
						fireListener(readed);
					}
				} catch (final IOException e) {
					log.log(Level.WARNING, "", e);
					log.info("exception");
				} catch (final ClassNotFoundException e) {
					log.log(Level.SEVERE,
							"dlaczego wiadomość odczytana jest nie castowalna na Message?",
							e);
					log.info("exception");
				}
				log.info("listener stopped");
			}
		};
		sender.start();
		listener.start();
		connected = true;
	}

	protected void fireListener(IMessage msg) {
		if (onNewMessageListener != null) {
			onNewMessageListener.onNewMessage(msg);
		}
	}

	protected void addToSendQueue(IMessage msg) {
		queue.add(msg);
	}

	/**
	 * wysyla Message na druga strone polaczenia
	 * 
	 * @param msg
	 *            - wiadomosc do wyslania
	 * @throws Exception
	 *             - dla klas dziedziczacych
	 */
	public synchronized void send(IMessage msg) throws Exception {
		addToSendQueue(msg);
	}

	/**
	 * Ustawia nowego listenera, ktory bedzie informowany o nowych wiadomosciach
	 * 
	 * @param listener
	 */
	public synchronized void setNewMessageListener(
			IMessageListener<IMessage> listener) {
		onNewMessageListener = listener;
	}

	public boolean isConnected() {
		return connected;
	}

	/**
	 * Zamyka polaczenie
	 * 
	 * @param sendPendingMessages
	 *            - true -> wylacza sendera, dopiero jak wysle wszystkie zalegle
	 *            wiadomosci
	 */
	public void shutdown(boolean sendPendingMessages) {
		listener.interrupt();
		if (sendPendingMessages) {
			try {
				queue.waitForEmpty();
			} catch (final InterruptedException e) {
				// TODO LOGowanie
				e.printStackTrace();
			}
		}
		sender.interrupt();
		try {
			input.close();
			output.close();
			socket.close();
		} catch (final IOException e) {
			// TODO LOGowanie
			e.printStackTrace();
		}
	}

	/**
	 * Zamyka polaczenie, mozliwa utrata niewyslanych wiadomosci
	 */
	public void shutdown() {
		shutdown(false);
	}

}
