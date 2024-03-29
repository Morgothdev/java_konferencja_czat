package konfa;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

@SuppressWarnings("javadoc")
public class SSLStore {

	static InputStream keystoreInput = Object.class
			.getResourceAsStream("/resources/keySSL");
	static InputStream truststoreInput = Object.class
			.getResourceAsStream("/resources/keySSL");

	public static void loadCertificate() throws Exception {
		System.out.println("key: " + keystoreInput);
		System.out.println("trust: " + truststoreInput);

		setSSLFactories(keystoreInput, "konfa1", truststoreInput);
		keystoreInput.close();
		truststoreInput.close();
	}

	private static void setSSLFactories(InputStream keyStream,
			String keyStorePassword, InputStream trustStream) throws Exception {
		// Get keyStore
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		System.out.println(keyStore);

		// if your store is password protected then declare it (it can be null
		// however)
		char[] keyPassword = keyStorePassword.toCharArray();
		System.out.println(keyPassword);

		// load the stream to your store
		keyStore.load(keyStream, keyPassword);

		// initialize a trust manager factory with the trusted store
		KeyManagerFactory keyFactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		System.out.println(keyFactory);
		keyFactory.init(keyStore, keyPassword);

		// get the trust managers from the factory
		KeyManager[] keyManagers = keyFactory.getKeyManagers();
		System.out.println(keyManagers);
		// Now get trustStore
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		System.out.println(trustStore);
		// if your store is password protected then declare it (it can be null
		// however)
		// char[] trustPassword = password.toCharArray();

		// load the stream to your store
		trustStore.load(trustStream, keyPassword);

		// initialize a trust manager factory with the trusted store
		TrustManagerFactory trustFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		System.out.println(trustFactory);
		trustFactory.init(trustStore);

		// get the trust managers from the factory
		TrustManager[] trustManagers = trustFactory.getTrustManagers();
		System.out.println(trustManagers);
		// initialize an ssl context to use these managers and set as default
		SSLContext sslContext = SSLContext.getInstance("SSL");
		System.out.println(sslContext);
		sslContext.init(keyManagers, trustManagers, null);
		SSLContext.setDefault(sslContext);
	}
}
