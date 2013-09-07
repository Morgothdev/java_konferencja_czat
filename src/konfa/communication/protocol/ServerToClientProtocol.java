package konfa.communication.protocol;

import java.io.IOException;
import java.net.Socket;

import konfa.message.IMessage;

/**
 * Klasa rozszerzająca Protocol do komunikacji Server->Klient
 * 
 * @author mateusz
 * 
 */
public class ServerToClientProtocol extends Protocol {

	@SuppressWarnings("javadoc")
	public ServerToClientProtocol(Socket connectedSocket,
			IMessageListener<IMessage> listener) throws IOException {
		super(connectedSocket, listener);

	}

}
