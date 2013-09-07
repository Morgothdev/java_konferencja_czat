package konfa.communication.protocol;


@SuppressWarnings("javadoc")
public interface IMessageListener<T> {
	public void onNewMessage(T msg);
}
