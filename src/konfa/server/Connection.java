package konfa.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import java.util.logging.Level;

import konfa.communication.protocol.IMessageListener;
import konfa.communication.protocol.ISendInfoListener;
import konfa.communication.protocol.MessagesQueue;
import konfa.communication.protocol.Protocol;
import konfa.communication.protocol.ServerToClientProtocol;
import konfa.message.IChangePropertyMessage;
import konfa.message.IFunctionalMessage;
import konfa.message.ILoginMessage;
import konfa.message.IMessage;
import konfa.message.IRegularMessage;
import konfa.message.IReturnInfoMessage;
import konfa.message.MessageFactory;

/**
 * Klasa reprezentująca oraz zarządzająca bezpośrednim połączeniem z klientem.
 * 
 * @author mateusz
 * 
 */
public class Connection implements IMessageListener<IMessage>,
		ISendInfoListener {

	private User user;
	private final MessagesManager manager = MessagesManager.getManager();
	private final MyLogger log = MyLogger.getLogger();
	private final ServerToClientProtocol protocol;
	private final Map<IMessage, Long> sended = new HashMap<IMessage, Long>();
	private boolean logged = false;

	/**
	 * @param socket
	 *            - socket z nawiązanym sprzętowym połączeniem
	 * @param msgQueue
	 *            - kolejka komunikatów, do której wysyłane sa odebrane
	 *            wiadomości
	 * @throws IOException
	 */
	public Connection(Socket socket, MessagesQueue<SrvMessage> msgQueue)
			throws IOException {
		protocol = new ServerToClientProtocol(socket, this);
		protocol.setSendInfoListener(this);
	}

	/**
	 * Zwraca użytkownika, z którym jest to połączenie nawiązane
	 * 
	 * @return połączony użytkownik
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Procedura logujaca klienta do serwera, wysyła klientowi informację o
	 * powodzeniu logowania.
	 * 
	 * @param userMail
	 *            mail klienta
	 * @param userPassword
	 *            hasło
	 * 
	 * @return true w przypadku sukcesu, wpp false
	 */
	public boolean logIn(String userMail, String userPassword) {
		if ((user = Users.getUserFromMail(userMail)) != null) {
			if (user.checkPassword(userPassword)) {
				log.info("password matched");
				final IReturnInfoMessage retInfo = MessageFactory
						.getReturnLoginMessage(Protocol.correctLogin);
				write(retInfo);
				sendOutstandingMessages();
				return true;
			} else {
				log.info("wrong password");
				final IReturnInfoMessage retInfo = MessageFactory
						.getReturnLoginMessage(Protocol.incorrectPassword);
				write(retInfo);
			}
		} else {
			log.info("wrong login");
			final IReturnInfoMessage retInfo = MessageFactory
					.getReturnLoginMessage(Protocol.incorrectUserMail);
			write(retInfo);
		}
		return false;
	}

	void sendOutstandingMessages() {
		try {
			final Queue<SrvMessage> outstanding = manager
					.getOutstandingMessages(user);
			while (!outstanding.isEmpty()) {
				write(outstanding.poll());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Wysyła Message do klienta, podając ją protokołowi do wysłania
	 * 
	 * @param msg
	 *            - wiadomość do wysłania
	 */
	public void write(IMessage msg) {
		try {
			protocol.send(msg);
		} catch (final Exception e) {
			// TODO na razie żaden wyjątek tu nie leci
			log.log(Level.WARNING, "", e);
		}

	}

	/**
	 * Wysyła Message do klienta, podając ją protokołowi do wysłania
	 * 
	 * @param ret
	 *            - wiadomość do wysłania
	 */
	public void write(SrvMessage ret) {
		log.info("write name " + ret.getUserName() + " content "
				+ ret.getContent() + " OBJECT:" + toString());
		sended.put(ret.toMessage(), ret.getID());
		try {
			protocol.send(ret.toMessage());
		} catch (final Exception e) {
			// TODO na razie żaden wyjątek tu nie leci
			log.log(Level.WARNING, "", e);
		}

	}

	/**
	 * Zwraca informację, czy połączenie jest dobre
	 * 
	 * @return true jeśli nie było dotąd problemów z połączeniem
	 */
	public boolean isConnected() {
		return protocol.isConnected();
	}

	/**
	 * Zamyka połączenie
	 */
	public void close() {
		protocol.shutdown();
	}

	/**
	 * @return informację, czy klient przeprowdził poprawne logowanie
	 */
	public boolean isLogged() {
		return logged;
	}

	@Override
	public void onNewMessage(IMessage msg) {
		if (msg.isFunctional()) {
			log.info("odczytano funkcyjną wiadomość: " + msg);
			final IFunctionalMessage funMsg = (IFunctionalMessage) msg;
			switch (funMsg.getFunction()) {
			case CHANGE_PROPERTY:
				final IChangePropertyMessage changeMsg = (IChangePropertyMessage) funMsg;
				switch (changeMsg.getChangeType()) {
				case CHANGE_MAIL:
					if (!user.setMail(changeMsg.getNewValue())) {
						final IReturnInfoMessage ret = MessageFactory
								.getReturnChangeMailMessage(Protocol.adresArleadyInUSe);
						write(ret);
					}
					break;
				case CHANGE_PASSWORD:
					user.setPassword(changeMsg.getNewValue());
					break;
				}
				break;
			case LOGIN:
				final ILoginMessage loginMsg = (ILoginMessage) funMsg;
				if (logged = logIn(loginMsg.getLogin(),
						loginMsg.getHashedPassword())) {
					log.info("loggin succed");
					log.info("logged User id: " + user.getID()
							+ " diplay_name " + user.getDisplayName());
				} else {
					log.info("login unsucced");
				}
				break;
			default:
				break;
			}
		} else {
			log.info("odtrzymno zwykłą wiadomość content ");
			final SrvMessage result = new SrvMessage((IRegularMessage) msg,
					user, Calendar.getInstance(TimeZone.getTimeZone("GMT")));
			manager.onNewMessage(result);
		}

	}

	@Override
	public void onSucces(IMessage msg) {
		final Long id = sended.get(msg);
		if (id == null) {
			log.info("funkcyjna wiadomosc " + " dostarczona");
		} else {
			sended.remove(msg);
			user.setLastReceivedMessage(id);
			log.info("wiadomosc content: " + " dostarczona");
		}

	}

	@Override
	public void onError(IMessage msg) {
		sended.remove(msg);
		log.info("wiadomosc content: " + " NIE DOSTARCZONA KURRRR......");
		// TODO reakcja, jesli wyslanie wiadomosci sie nie powiodlo
	}

}
