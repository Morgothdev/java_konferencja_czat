package konfa.server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import konfa.communication.protocol.StringHasherToSHA256;

/**
 * Klasa opisująca użytkownika i dane z nim związane, jest obiektem używanym do
 * zapisu w bazie przez Hibernate
 * 
 * @author mateusz
 * 
 */
@Entity
@Table(name = "USERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "MAIL" }) })
public class User {

	@Id
	@GeneratedValue
	@Column(name = "USER_ID")
	private Long id;

	@Column(name = "LAST_MESSAGE")
	private Long lastMessage = new Long(-1);

	@Column(name = "DISPLAY_NAME")
	private String displayName;

	@Column(name = "MAIL")
	private String mail;

	@Column(name = "PASSWORD")
	private String password;

	@Transient
	private OnUserChangeListener onChangeListener;

	protected User() {
	}

	public String getMail() {
		return this.mail;
	}

	public Long getLastMessageID() {
		return this.lastMessage;
	}

	public void setDisplayName(String newDisplayName) {
		this.displayName = newDisplayName;
		try {
			this.onChangeListener.onChange(this);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public boolean setMail(String newMail) {
		final String oldMail = this.mail;
		this.mail = newMail;
		try {
			this.onChangeListener.onChange(this);
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			this.mail = oldMail;
			return false;
		}
	}

	public void setPassword(String newPassword) {
		this.password = newPassword;
		try {
			this.onChangeListener.onChange(this);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setLastReceivedMessage(Long lastMessage) {
		this.lastMessage = lastMessage;
		try {
			this.onChangeListener.onChange(this);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void fireOnChangeListener() {
		try {
			this.onChangeListener.onChange(this);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public String getPassword() {
		return this.password;
	}

	public void setOnChangeListener(OnUserChangeListener listener) {
		this.onChangeListener = listener;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public User(String displayName, String mail, String password,
			OnUserChangeListener listener) {
		this.displayName = displayName;
		this.mail = mail;
		this.password = StringHasherToSHA256.hash(password);
		this.onChangeListener = listener;
	}

	public User(String displayName, String mail, String password) {
		this.displayName = displayName;
		this.mail = mail;
		this.password = StringHasherToSHA256.hash(password);
	}

	public Long getID() {
		return this.id;
	}

}
