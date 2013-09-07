package konfa.server;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

@SuppressWarnings("javadoc")
public class SSLKServer extends Thread {

	public SSLKServer() throws IOException {

		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory
				.getDefault();

		@SuppressWarnings("unused")
		SSLServerSocket serverSocket = (SSLServerSocket) factory
				.createServerSocket(7171);

	}
}
