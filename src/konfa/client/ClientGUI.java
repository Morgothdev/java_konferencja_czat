package konfa.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

//TODO: poprawic rozmiar okien, wyglad, etc ... w chuj do zrobienia
/**
 * @author dan
 * 
 *         GUI klienta konferencji
 */
public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 3854213403984732658L;
	private JPanel contentPane;
	private JPanel messagesViewer;
	private JScrollPane scrollPane;
	public JButton sendButton;
	public JTextArea textArea;

	public ClientGUI() {
		createAndShowGUI();
	}

	private void createAndShowGUI() {
		this.setTitle("morgaroth");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setMinimumSize(new Dimension(300, 300));
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu settingsMenu = new JMenu("Settings");
		menuBar.add(settingsMenu);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		createAndShowGUIHeader();
		createAndShowGUIMessagesViewer();
		createAndShowGUIEditorPane();
		createAndShowRightPanel();

		JMenuItem loggingDataMenu = new JMenuItem("logging");
		settingsMenu.add(loggingDataMenu);
		JCheckBoxMenuItem toggleEnter = new JCheckBoxMenuItem(
				"enter send message");

		final KeyListener listener = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					sendButton.doClick();
					arg0.consume();
				}
			}
		};

		toggleEnter.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					textArea.addKeyListener(listener);
				} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
					textArea.removeKeyListener(listener);
				}
			}
		});
		toggleEnter.doClick();
		settingsMenu.add(toggleEnter);

		MenuListener menuListener = new MenuListener();
		JMenuItem deleteSettings = new JMenuItem("delete settings");
		deleteSettings.addActionListener(menuListener);
		settingsMenu.add(deleteSettings);

		settingsMenu.addSeparator();

		JMenuItem exit = new JMenuItem("exit");
		exit.addActionListener(menuListener);
		settingsMenu.add(exit);

		pack();
		setVisible(true);
	}

	private void createAndShowGUIHeader() {
		JPanel headPanel = new JPanel();
		contentPane.add(headPanel, BorderLayout.NORTH);
	}

	private void createAndShowGUIMessagesViewer() {
		messagesViewer = new JPanel();

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(messagesViewer);
		messagesViewer
				.setLayout(new BoxLayout(messagesViewer, BoxLayout.Y_AXIS));
		// JPanel panel = new JPanel();
		// messagesViewer.add(panel);
		scrollPane.setMaximumSize(new Dimension(400, 300));
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * funkcja przewijajaca okienko wiadomosci do aktualnej
	 */
	public void scroll() { // TODO: poprawic scrollowanie
		scrollPane.getVerticalScrollBar().setValue(messagesViewer.getHeight());
	}

	private void createAndShowGUIEditorPane() {
		JPanel editorPanel = new JPanel();
		contentPane.add(editorPanel, BorderLayout.SOUTH);
		editorPanel.setLayout(new GridLayout(3, 0, 0, 0));

		JPanel addingsMessagePanel = new JPanel();
		editorPanel.add(addingsMessagePanel);
		addingsMessagePanel.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel contentMessagePanel = new JPanel();
		editorPanel.add(contentMessagePanel);
		contentMessagePanel.setLayout(new GridLayout(1, 0, 0, 0));

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		DefaultCaret caretOfDisplay = (DefaultCaret) textArea.getCaret();
		caretOfDisplay.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		contentMessagePanel.add(textArea);

		sendButton = new JButton("send");
		sendButton.setHorizontalAlignment(SwingConstants.RIGHT);
		sendButton.setMaximumSize(new Dimension(50, contentMessagePanel
				.getHeight()));
		contentMessagePanel.add(sendButton);

		JButton insertImageButton = new JButton("image", new ImageIcon(
				getClass().getResource("/resources/icons/insert.png")));
		addingsMessagePanel.add(insertImageButton);

		JButton insertEmoticonButton = new JButton("smileys", new ImageIcon(
				getClass().getResource("/resources/icons/emotes.png")));
		addingsMessagePanel.add(insertEmoticonButton);

		JButton showHistoryButton = new JButton("history", new ImageIcon(
				getClass().getResource("/resources/icons/history.png")));
		addingsMessagePanel.add(showHistoryButton);

		JPanel actionPanel = new JPanel();
		editorPanel.add(actionPanel);
	}

	private void createAndShowRightPanel() {
		JPanel rightPanel = new JPanel();
		contentPane.add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new GridLayout(10, 1));

	}

	/**
	 * @return komponent zawierajacy wyswietlane wiadomosci
	 */
	public JPanel getMessagesViewer() {
		return messagesViewer;
	}
}
