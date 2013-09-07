package konfa.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import konfa.Consts;
import konfa.communication.protocol.ClientToServerProtocol;
import konfa.communication.protocol.ILoggingListener;
import konfa.communication.protocol.IMessageListener;
import konfa.communication.protocol.NotLoggedException;
import konfa.message.IChangePropertyMessage;
import konfa.message.IFunctionalMessage;
import konfa.message.IMessage;
import konfa.message.IRegularMessage;
import konfa.message.Message;
import konfa.message.Message.FUNCTION;
import konfa.message.MessageFactory;

/**
 * Główna klasa klienta, zadaniem jej jest uruchomienie klienta jako takiego
 * włącznie z GUI, nawiązaniem połaczenia z serwerem, uruchomieniem listenera
 * nasłuchującego na wiadomości od serwera.
 * 
 * @author mateusz
 */
public class KClient extends Thread implements ILoggingListener,
		IMessageListener<IMessage> {

	private final Logger log = Logger.getLogger("clientLogger");
	private final ClientToServerProtocol protocol;
	private MessageListener messageListener;
	private FunctionalMessageListener funMessageListener;

	/**
	 * Główny i jedyny kontruktor, robi to co w opisie klasy;] Rzuca wyjątkami,
	 * jeśli nie moze sie polaczyc
	 * 
	 * @param connectToLocalhost
	 * 
	 * @throws UnknownHostException
	 *             jesli nie da sie polaczyc
	 * @throws IOException
	 *             tez z tworzenia socketa
	 * @throws ClassNotFoundException
	 */
	public KClient(boolean connectToLocalhost) throws UnknownHostException,
			IOException, ClassNotFoundException {

		// SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory
		// .getDefault();
		//
		// SSLSocket socket = (SSLSocket) factory.createSocket(
		// connectToLocalhost ? "localhost" : "5.231.60.109",
		// Consts.portToListenOnClientsConnections);
		final Socket socket = new Socket(connectToLocalhost ? "localhost"
				: "5.231.60.109", Consts.portToListenOnClientsConnections);

		final ClientGUI gui = new ClientGUI();
		messageListener = new MessageListener(gui);
		funMessageListener = new FunctionalMessageListener();

		protocol = new ClientToServerProtocol(socket, this, this);

		if (!ClientSettings.getInstance().isComplete()) {
			log.info("missing logging data, exit");
			JOptionPane.showMessageDialog(null, "Login and password must be!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		protocol.logIn(ClientSettings.getInstance().getLogin(), ClientSettings
				.getInstance().getPassword());

		gui.sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String content = gui.textArea.getText();
				try {
					protocol.send(new Message(content));
					log.info("wiadomość wysłana " + content);
					gui.textArea.setText("");
				} catch (final NotLoggedException e1) {
					// TODO tutaj okienka, ponieważ protocol rzuca wyjątek
					// jesli nie jest zalogowane polaczenie - info, ze
					// wiadomosc nie zostanie wyslana
					e1.printStackTrace();
				} catch (final Exception e1) {
					e1.printStackTrace();
				}

			}
		});

	}

	@Override
	public void onSuccesLogin() {
		log.info("login succes");
	}

	@Override
	public void onFailureLogin(int przyczyna) {

		JOptionPane.showMessageDialog(null, "Incorrect login or password!",
				"Error", JOptionPane.ERROR_MESSAGE);
		ClientSettings.getInstance().removeSettings();

		// TODO info i reakcja na niepoprawne dane do logowania, najlepiej
		// okienko o poprawione dane
	}

	@Override
	public void onNewMessage(IMessage msg) {
		log.info("wiadomosc odebrana, content: ");
		if (msg.isFunctional()) {
			funMessageListener.onNewMessage((IFunctionalMessage) msg);
		} else {
			messageListener.onNewMessage((IRegularMessage) msg);
		}

	}

	public void setPassword(String newPassword) {
		final IChangePropertyMessage msg = MessageFactory
				.getChangePropertyMessage(FUNCTION.CHANGE_PASSWORD, newPassword);
		try {
			protocol.send(msg);
		} catch (final NotLoggedException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}