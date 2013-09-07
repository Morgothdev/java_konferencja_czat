package konfa.communication.protocol;

@SuppressWarnings("javadoc")
public interface ILoggingListener {
	final int WRONG_MAIL = 1;
	final int WRONG_PASSWORD = 2;

	void onSuccesLogin();

	/**
	 * @param przyczyna
	 *            - jedna ze staych z tego interfejsu
	 */
	void onFailureLogin(int przyczyna);// TODO przetumaczyc:p
}
