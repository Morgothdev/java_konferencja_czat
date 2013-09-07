package konfa.client;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Klasa startująca server, przy okazji inicjalizuje logger
 * 
 * @author mateusz
 * 
 */
public class Starter {
	/**
	 * @param args
	 *            localhost || local || loc uruchamia Klienta do połączenia do
	 *            localhosta
	 */
	public static void main(String[] args) {
		try {
			Logger.getLogger("clientLogger").addHandler(
					new FileHandler("log.out"));
			// TODO ogarnąć jak wczytywać pliki z wnętrza jara
			// try {
			// SSLStore.loadCertificate();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			boolean connectToLocalhost = args.length > 0
					&& (args[0].equals("localhost") || args[0].equals("local") || args[0]
							.equals("loc"));
			new KClient(connectToLocalhost);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
