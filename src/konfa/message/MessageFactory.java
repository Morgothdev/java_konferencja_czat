package konfa.message;

import konfa.message.Message.FUNCTION;

public class MessageFactory {
	public static ILoginMessage getLoginMessage(String login,
			String hashedPassword) {
		final Message ret = new Message();
		ret.makeLoginMessage(login, hashedPassword);
		return ret;
	}

	public static IChangePropertyMessage getChangePropertyMessage(
			FUNCTION propertyToChange, String newValue) {
		final Message ret = new Message();
		ret.makeChangePropertyMessage(propertyToChange, newValue);
		return ret;
	}

	public static IReturnInfoMessage getReturnLoginMessage(String resultInfo) {
		final Message ret = new Message();
		ret.makeReturnInfoMessage(FUNCTION.RETURN_LOGIN, resultInfo);
		return ret;
	}

	public static IReturnInfoMessage getReturnChangeMailMessage(
			String resultInfo) {
		final Message ret = new Message();
		ret.makeReturnInfoMessage(FUNCTION.RETURN_LOGIN, resultInfo);
		return ret;
	}

}
