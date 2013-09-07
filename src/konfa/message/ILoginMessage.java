package konfa.message;

public interface ILoginMessage extends IFunctionalMessage {

	String getLogin();

	String getHashedPassword();

	void makeLoginMessage(String login, String hashedPassword);

}