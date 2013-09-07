package konfa.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author dan
 * 
 *         obsługa akcji do menu
 */
public class MenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("delete settings"))
			ClientSettings.getInstance().removeSettings();
		if (e.getActionCommand().equals("exit"))
			System.exit(0);
	}

}
