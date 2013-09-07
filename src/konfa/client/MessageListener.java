package konfa.client;

import java.util.logging.Logger;

import javax.swing.JPanel;

import konfa.communication.protocol.IMessageListener;
import konfa.message.IRegularMessage;

public class MessageListener implements IMessageListener<IRegularMessage> {

	private final ClientGUI gui;

	public MessageListener(ClientGUI gui) {
		this.gui = gui;
	}

	@Override
	public void onNewMessage(IRegularMessage msg) {
		final JPanel msgPanel = MessagePanelFactory.getMessagePanel(msg);
		Logger.getLogger("clientLogger").info("received message");
		gui.getMessagesViewer().add(msgPanel);
		gui.getMessagesViewer().revalidate();
		gui.scroll();
	}
}
