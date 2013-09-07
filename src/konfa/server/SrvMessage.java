package konfa.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import konfa.message.IRegularMessage;
import konfa.message.Message;

/**
 * dekorator do Message używany w serwerze, gdzie przydaje się wiecej
 * własciwiości wiadomości klasa używana do zapisywania w bazie
 * 
 * @author mateusz
 * 
 */
@Entity
@Table(name = "ARCH")
public class SrvMessage {

	@SuppressWarnings("javadoc")
	public static long count = 1;
	@Id
	@GeneratedValue
	@Column(name = "MESSAGE_ID")
	private Long id = null;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user = null;

	@Column(name = "CONTENT")
	private String content = null;

	@Column(name = "DATE")
	private Calendar date = null;

	private Message relatedMessage = null;

	/**
	 * Generuje zwykłą Message do wysłania do klienta
	 * 
	 * @return zwykła wiadomość do wysłania do klienta
	 */
	public synchronized Message toMessage() {
		if (relatedMessage == null) {
			relatedMessage = new Message();
			relatedMessage
					.setContent(content)
					.setDate(
							new SimpleDateFormat("DD MM yyyy", Locale.ENGLISH)
									.format(date.getTime()))
					.setUserName(user.getDisplayName());
		}
		return relatedMessage;
	}

	@SuppressWarnings("javadoc")
	public SrvMessage(IRegularMessage message, User user, Calendar date) {
		this.user = user;
		this.date = date;
		content = message.getContent();
		id = count++;
	}

	@SuppressWarnings("javadoc")
	public String getUserName() {
		return user.getDisplayName();
	}

	@SuppressWarnings("javadoc")
	public String getContent() {
		return content;
	}

	@SuppressWarnings("javadoc")
	public Long getID() {
		return id;
	}

	// used by hibernate
	@SuppressWarnings("unused")
	private SrvMessage() {
	}

	@Override
	public String toString() {
		return new StringBuilder("from ").append(user.getDisplayName())
				.append(" content: \"").append(content).append("\" id: ")
				.append(id).toString();
	}
}
