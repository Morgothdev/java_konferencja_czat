package konfa.communication.protocol;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringHasherToSHA256 {
	public static String hash(String stringToHash) {
		final MessageDigest hasher = getHasher();
		hasher.update(stringToHash.getBytes());
		final byte[] sum = hasher.digest();
		final BigInteger bi = new BigInteger(1, sum);
		return String.format("%0" + (sum.length << 1) + "x", bi);
	}

	private static MessageDigest getHasher() {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return messageDigest;
	}
}
