package konfa.message;

public interface IRegularMessage extends IMessage {

	public String getUserName();

	public String getDate();

	public String getContent();

	void makeRegularMessage(String date, String userName, String content);

}