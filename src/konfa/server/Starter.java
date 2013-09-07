package konfa.server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import konfa.Consts;
import konfa.server.service.LogServer;

/**
 * Klasa startująca serwer, użyta glównie dla przygotowania loggera i
 * uruchomienia KServera
 * 
 * @author mateusz
 */
public class Starter {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyLogger logger = MyLogger.getLogger();
		try {
			logger.setLevel(Level.ALL);
			logger.addHandler(new FileHandler("log.out"));
			logger.addHandler(new LogServer(Consts.portToListenOnLogsClients));
			logger.log(Level.INFO, "file handler setted");
			// SSLStore.loadCertificate();
			KServer srv = new KServer();
			srv.start();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Starter", e);
		}
	}

}
