package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Time;

@Entity(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue
    private long ID;
    private Date date;
    private Time time;
    private long chatId;
    private String text;

    public NotificationTask(long ID, Date date, Time time, long chatId, String text) {
        this.ID = ID;
        this.date = date;
        this.time = time;
        this.chatId = chatId;
        this.text = text;
    }

    public NotificationTask() {
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return date + " " + time + " — " + text;
    }
}
