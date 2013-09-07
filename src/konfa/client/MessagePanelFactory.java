package konfa.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import konfa.message.IRegularMessage;

public class MessagePanelFactory { // TODO: poprawic albo zmienic

	public static JPanel getMessagePanel(IRegularMessage msg) {
		final JPanel res = new JPanel();
		final JPanel head = new JPanel(new GridLayout(1, 0, 50, 50));
		res.setLayout(new BorderLayout());
		JLabel label = new JLabel(msg.getUserName());
		label.setHorizontalTextPosition(SwingConstants.LEFT);
		head.add(label);
		label = new JLabel(msg.getDate());
		label.setHorizontalTextPosition(SwingConstants.RIGHT);
		head.add(label);
		res.add(head, BorderLayout.NORTH);
		final JTextArea body = new JTextArea(msg.getContent());
		body.setLineWrap(true);
		body.setWrapStyleWord(true);
		body.setEnabled(false);
		res.add(body, BorderLayout.CENTER);
		res.setBorder(BorderFactory.createRaisedBevelBorder());
		body.revalidate();
		res.revalidate();
		/*
		 * System.out.println(msg.getMinimumSize()); int width =
		 * msg.getMaximumSize().width; int height = msg.getMinimumSize().height;
		 * msg.setMaximumSize(new Dimension(width, height));
		 */
		res.setPreferredSize(res.getMinimumSize());
		return res;
	}

}
