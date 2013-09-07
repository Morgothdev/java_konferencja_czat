/**
 * 
 */
package konfa.communication.protocol;

import konfa.message.IMessage;

/**
 * @author mateusz
 * 
 * 
 */
public interface ISendInfoListener {
	@SuppressWarnings("javadoc")
	void onSucces(IMessage msg);

	@SuppressWarnings("javadoc")
	void onError(IMessage msg);
}
