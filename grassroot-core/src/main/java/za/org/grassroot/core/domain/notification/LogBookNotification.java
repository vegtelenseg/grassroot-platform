package za.org.grassroot.core.domain.notification;

import za.org.grassroot.core.domain.*;
import za.org.grassroot.core.enums.NotificationType;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LogBookNotification extends Notification {
	@ManyToOne
	@JoinColumn(name = "log_book_id")
	private LogBook logBook;

	@Override
	public NotificationType getNotificationType() {
		return NotificationType.LOGBOOK;
	}

	protected LogBookNotification() {
		// for JPA
	}

	@Override
	protected void appendToString(StringBuilder sb) {
		sb.append(", logBook=").append(logBook);
	}

	protected LogBookNotification(User target, String message, LogBookLog logBookLog) {
		this(target, message, logBookLog, logBookLog.getLogBook());
	}

	protected LogBookNotification(User target, String message, ActionLog actionLog, LogBook logBook) {
		super(target, message, actionLog);
		this.logBook = logBook;
	}

	public LogBook getLogBook() {
		return logBook;
	}
}
