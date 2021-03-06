package za.org.grassroot.core.domain.notification;

import za.org.grassroot.core.domain.Account;
import za.org.grassroot.core.domain.AccountLog;
import za.org.grassroot.core.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("FREE_FORM_MESSAGE")
public class FreeFormMessageNotification extends AccountNotification {

	protected FreeFormMessageNotification() {
	}

	public FreeFormMessageNotification(User destination, String message, AccountLog accountLog) {
		super(destination, message, accountLog);
	}
}
