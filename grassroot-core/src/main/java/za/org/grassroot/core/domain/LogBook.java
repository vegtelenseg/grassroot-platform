package za.org.grassroot.core.domain;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aakilomar on 12/3/15.
 */
@Entity
@Table(name = "log_book",
        indexes = {@Index(name = "idx_log_book_group_id",  columnList="group_id", unique = false),
        @Index(name = "idx_log_book_completed", columnList="completed",  unique = false),
        @Index(name = "idx_log_book_retries_left", columnList = "number_of_reminders_left_to_send",unique = false),
                @Index(name = "idx_log_book_assigned_to", columnList = "assigned_to_user_id",unique = false),
                @Index(name = "idx_log_book_replicated_group_id", columnList = "replicated_group_id",unique = false)})

public class LogBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @Basic
    @Column(name="created_date_time", insertable = true, updatable = false)
    private Date createdDateTime;
    @Basic
    @Column(name="created_by_user_id")
    private Long createdByUserId;
    @Basic
    @Column(name="group_id")
    private Long groupId;
    @Basic
    @Column
    private boolean completed;
    @Basic
    @Column(name="completed_by_user_id")
    private Long completedByUserId;
    @Basic
    @Column(name="completed_date")
    private Date completedDate;
    @Basic
    @Column
    private String message;
    @Basic
    @Column(name="assigned_to_user_id")
    private Long assignedToUserId;
    @Basic
    @Column(name="replicated_group_id")
    private Long replicatedGroupId;
    @Basic
    @Column(name="action_by_date")
    private Date actionByDate;
    /*
    Minus value will send a reminder before actionByDate, Plus value will send a reminder x minutes after
    actionByDate
     */
    @Basic
    @Column(name="reminder_minutes")
    private int reminderMinutes;
    @Basic
    @Column(name="number_of_reminders_left_to_send")
    private int numberOfRemindersLeftToSend;


    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        if (createdDateTime == null) {
            createdDateTime = new Date();
        }
    }

    // Constructors


    public LogBook() {
    }

    public LogBook(Long groupId, String message, Date actionByDate) {
        this.groupId = groupId;
        this.message = message;
        this.actionByDate = actionByDate;
    }

    public LogBook(Long groupId, String message, Date actionByDate, Long assignedToUserId) {
        this.groupId = groupId;
        this.message = message;
        this.actionByDate = actionByDate;
        this.assignedToUserId = assignedToUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getCompletedByUserId() {
        return completedByUserId;
    }

    public void setCompletedByUserId(Long completedByUserId) {
        this.completedByUserId = completedByUserId;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public Long getReplicatedGroupId() {
        return replicatedGroupId;
    }

    public void setReplicatedGroupId(Long replicatedGroupId) {
        this.replicatedGroupId = replicatedGroupId;
    }

    public Date getActionByDate() {
        return actionByDate;
    }

    public void setActionByDate(Date actionByDate) {
        this.actionByDate = actionByDate;
    }

    public int getReminderMinutes() {
        return reminderMinutes;
    }

    public void setReminderMinutes(int reminderMinutes) {
        this.reminderMinutes = reminderMinutes;
    }

    public int getNumberOfRemindersLeftToSend() {
        return numberOfRemindersLeftToSend;
    }

    public void setNumberOfRemindersLeftToSend(int numberOfRemindersLeftToSend) {
        this.numberOfRemindersLeftToSend = numberOfRemindersLeftToSend;
    }

    @Override
    public String toString() {
        return "LogBook{" +
                "id=" + id +
                ", createdDateTime=" + createdDateTime +
                ", createdByUserId=" + createdByUserId +
                ", groupId=" + groupId +
                ", completed=" + completed +
                ", completedByUserId=" + completedByUserId +
                ", completedDate=" + completedDate +
                ", message='" + message + '\'' +
                ", assignedToUserId=" + assignedToUserId +
                ", replicatedGroupId=" + replicatedGroupId +
                ", actionByDate=" + actionByDate +
                ", reminderMinutes=" + reminderMinutes +
                ", numberOfRemindersLeftToSend=" + numberOfRemindersLeftToSend +
                '}';
    }
}