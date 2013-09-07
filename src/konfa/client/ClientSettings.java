package konfa.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Klasa, która ma przechowywać ustawienia i dane użyszkodnika, na razie login i
 * hasło, jest singletonem, zapisuje sie używając serializacji do pliku podanego
 * w zmiennej PATH. zapisuje sama, nie trzeba z zewnątrz tego wywoływać, settery
 * mają za zadanie zapisywać dane po uaktualnieniu. przy pierwszym użyciu należy
 * sprawdzić, czy ustawienia są prawdiłowo zainicjalizowane, sprawdzając to
 * metodą isComplete(). Jesli ustawienia są złe, a sprawdzenie nie nastąpi, to w
 * getterach jest rzucany wyjątek
 * 
 * @author mateusz
 */
public class ClientSettings implements Serializable {

	private static final long serialVersionUID = -4326192937992003120L;
	private static ClientSettings instance = null;
	private String login;
	private String password;
	private static String PATH = "settings/passwd";
	private boolean complete = true;

	private ClientSettings() {
	}

	/**
	 * statyczna metoda ze wzorca singletonowego do odbierania obiektu
	 * 
	 * @return instancja ustawień, nigdy null
	 */
	public static ClientSettings getInstance() {
		// jesli nie bedzie inicjalizacji jeszcze, to inicjalizujemy
		if (instance == null) {
			if (!readSettings()) {
				// jesli nie udalo sie wyczytac z pliku
				instance = new ClientSettings();
				if (!instance.setDefaultSettings()) {
					// nie udalo sie tez wcztac domyslnych ustawien
					instance.complete = false;
				}
			}
		}
		return instance;
	}

	/**
	 * Zwraca informacje o kompletnosci ustawień, konieczne do sprawdzenia przy
	 * pierwszym użyciu singletona
	 * 
	 * @return true, jeśli ustawienia są kompletne
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Ustawia nowy login użytkownika, po czym zapisuje ustawienia na dysk
	 * 
	 * @param login
	 *            - nowy login do ustawienia
	 */
	public void setLogin(String login) {
		this.login = login;
		save();
	}

	/**
	 * Ustawia nowe hasło, po czym zapisuje ustawienia na dysk
	 * 
	 * @param password
	 *            - hasło użyszkodnika
	 */
	public void setPassword(String password) {
		this.password = password;
		save();
	}

	/**
	 * @return login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Wczytuje ustawienia z pliku podanego w zmiennej PATH
	 * 
	 * @return true jeśli wczytywanie się powiodło
	 */
	private static boolean readSettings() {
		File passwd = new File(PATH);
		if (passwd.exists()) {
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(new FileInputStream(passwd));
				try {
					instance = (ClientSettings) in.readObject();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			Logger.getLogger("clientLogger").info("settings loaded");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Ustawia domyślne ustawienia, lub pobiera je od użyszkodnika
	 * 
	 * @return true, jesli wszystkie wymagane pola zostały ustawione
	 */
	private boolean setDefaultSettings() {
		JTextField loginField = new JTextField();
		if (JOptionPane.showConfirmDialog(null, loginField, "Enter username",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			instance.login = loginField.getText().length() == 0 ? null
					: loginField.getText();
		} else {
			Logger.getLogger("clientLogger").info("login not entered");
			return false;
		}

		JTextField passwordField = new JPasswordField(20);
		if (JOptionPane.showConfirmDialog(null, passwordField,
				"Enter password", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			instance.password = passwordField.getText();
		} else {
			Logger.getLogger("clientLogger").info("password not entered");
			return false;
		}
		save();
		return true;
	}

	/**
	 * Zapisuje ustawienia na dysk, na razie nie zwraca informacji o powodzeniu,
	 * ale warto by się nad tym zastanowić, dobrze byłoby boolem po prostu
	 */
	private void save() {
		File passwd = new File(PATH);
		new File("settings").mkdir();
		try {
			passwd.createNewFile();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(new FileOutputStream(passwd));
				try {
					out.writeObject(instance);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Logger.getLogger("clientLogger").info("settings saved");
	}

	/**
	 * funkcja usuwajaca zapisane ustawienia
	 */
	public void removeSettings() {
		new File(PATH).delete();
	}

}
