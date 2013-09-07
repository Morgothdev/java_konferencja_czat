package konfa.message;

import konfa.message.Message.FUNCTION;

public interface IFunctionalMessage extends IMessage {

	FUNCTION getFunction();
}
