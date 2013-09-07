package konfa.message;

import konfa.message.Message.FUNCTION;

public interface IReturnInfoMessage extends IFunctionalMessage {

	String getResult();

	void makeReturnInfoMessage(FUNCTION returnType, String returnInfo);
}
