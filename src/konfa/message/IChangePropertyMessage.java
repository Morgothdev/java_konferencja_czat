package konfa.message;

import konfa.message.Message.FUNCTION;

public interface IChangePropertyMessage extends IFunctionalMessage {

	FUNCTION getChangeType();

	String getNewValue();

	void makeChangePropertyMessage(FUNCTION propertyToChange, String newValue);

}