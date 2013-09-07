package konfa.communication.protocol;

import java.io.IOException;
import java.net.Socket;

import konfa.message.IFunctionalMessage;
import konfa.message.IMessage;
import konfa.message.IReturnInfoMessage;
import konfa.message.Message.FUNCTION;

/**
 * Specjalizowany protokol do komunikacji klient -->> server
 * 
 * @author mateusz
 * 
 */
public class ClientToServerProtocol extends Protocol {

	private boolean logged;
	private final ILoggingListener onLoggingListener;

	@SuppressWarnings("javadoc")
	public ClientToServerProtocol(Socket connectedSocket,
			ILoggingListener loginListener,
			IMessageListener<IMessage> messageListener) throws IOException {
		super(connectedSocket, messageListener);
		onLoggingListener = loginListener;
	}

	@SuppressWarnings("javadoc")
	public void logIn(String mail, String password) {
		final String hashedPassword = StringHasherToSHA256.hash(password);
		final IMessage loginMessage = konfa.message.MessageFactory
				.getLoginMessage(mail, hashedPassword);
		addToSendQueue(loginMessage);

		// log.info("login&password sended");
	}

	@Override
	protected void fireListener(IMessage msg) {
		if (msg.isFunctional()) {
			IFunctionalMessage funMsg = (IFunctionalMessage) msg;
			if (funMsg.getFunction() == FUNCTION.RETURN_LOGIN) {
				IReturnInfoMessage retMsg = (IReturnInfoMessage) funMsg;
				if (retMsg.getResult().equals(Protocol.correctLogin)) {
					logged = true;
					onLoggingListener.onSuccesLogin();
				} else {
					logged = false;
					if (retMsg.getResult().equals(Protocol.incorrectPassword)) {
						onLoggingListener
								.onFailureLogin(ILoggingListener.WRONG_PASSWORD);
					} else if (retMsg.getResult().equals(
							Protocol.incorrectUserMail)) {
						onLoggingListener
								.onFailureLogin(ILoggingListener.WRONG_MAIL);
					} else {
						// TODO log jaki o nieznanej odpowiedzi z serwera
						// cos w stylu WTF?!
					}
				}
			}
		} else {
			super.fireListener(msg);
		}
	}

	/**
	 * Przyjmuje wiadomosc do wyslania
	 * 
	 * @throws NotLoggedException
	 *             jesli probuje sie wyslac wiadomosc, a nie zostalo jeszcze
	 *             przeprowadzone poprawne logowanie
	 * @see konfa.communication.protocol.Protocol#send(konfa.message.Message)
	 */
	@Override
	public void send(IMessage msg) throws NotLoggedException, Exception {
		if (!logged) {
			throw new NotLoggedException();
		}
		super.send(msg);
	}

}
