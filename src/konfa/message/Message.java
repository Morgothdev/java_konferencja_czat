package konfa.message;

/**
 * Klasa Message, jest to klasa, której obiekty są przesyłane przez połączenie
 * klient-server ma kilka pół jak widać, używanych różnie w zależności, od tego,
 * czy jest to wiadomość zwykła, czy funkcyjna.
 * 
 * @author mateusz
 */
public class Message implements java.io.Serializable, ILoginMessage,
		IChangePropertyMessage, IReturnInfoMessage, IRegularMessage {
	private static final long serialVersionUID = 7328789168159096725L;

	public enum FUNCTION {
		LOGIN, CHANGE_MAIL, CHANGE_PASSWORD, CHANGE_PROPERTY, RETURN_LOGIN;
	}

	/**
	 * Wyświetlana nazwa użytkownika, nie mylić z loginem ani jakimkolwiek innym
	 * kluczem
	 */
	private String userName;
	/**
	 * Data wiadomości, ustalana przez serwer jako data otrzymania wiadomości od
	 * klienta
	 */
	private String date;
	private String content;
	private boolean functional;

	public Message(String userName, String date, String content) {
		this.userName = userName;
		this.date = date;
		this.content = content;
	}

	public Message(String content) {
		this.content = content;
	}

	public Message() {
	}

	@Override
	public String getUserName() {
		return userName;
	}

	public Message setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	@Override
	public String getDate() {
		return date;
	}

	public Message setDate(String date) {
		this.date = date;
		return this;
	}

	@Override
	public String getContent() {
		return content;
	}

	public Message setContent(String content) {
		this.content = content;
		return this;
	}

	@Override
	public boolean isFunctional() {
		return functional;
	}

	@Override
	public FUNCTION getFunction() {
		return FUNCTION.valueOf(date);
	}

	@Override
	public String getLogin() {
		return userName;
	}

	@Override
	public String getHashedPassword() {
		return content;
	}

	private void setFunctional(FUNCTION function) {
		date = function.name();
		functional = true;
	}

	@Override
	public void makeLoginMessage(String login, String hashedPassword) {
		setFunctional(FUNCTION.LOGIN);
		userName = login;
		content = hashedPassword;
	}

	@Override
	public FUNCTION getChangeType() {
		return FUNCTION.valueOf(userName);
	}

	@Override
	public String getNewValue() {
		return content;
	}

	@Override
	public void makeChangePropertyMessage(FUNCTION propertyToChange,
			String newValue) {
		setFunctional(FUNCTION.CHANGE_PROPERTY);
		userName = propertyToChange.name();
		content = newValue;
	}

	@Override
	public String getResult() {
		return content;
	}

	@Override
	public void makeRegularMessage(String date, String userName, String content) {
		this.userName = userName;
		this.date = date;
		this.content = content;
	}

	@Override
	public void makeReturnInfoMessage(FUNCTION returnType, String returnInfo) {
		setFunctional(returnType);
		content = returnInfo;
	}

}
