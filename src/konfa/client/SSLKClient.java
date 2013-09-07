package konfa.client;

import java.io.IOException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

@SuppressWarnings("javadoc")
public class SSLKClient extends Thread {

	public SSLKClient() throws SecurityException, IOException {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory
				.getDefault();
		@SuppressWarnings("unused")
		SSLSocket socket = (SSLSocket) factory.createSocket("5.231.60.109",
				7171);

	}
}