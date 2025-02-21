package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import liquibase.pro.packaged.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.UnparsableDataTimeException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {

    private Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);
    private NotificationRepository repository;
    @Value("$telegram.bot.token")
    private String bt;
    @Autowired
    private TelegramBot telegramBot;

    public NotificationTaskService(NotificationRepository repository) {
        this.repository = repository;
    }

    public NotificationTask newNotification(String text, Long chatId) throws UnparsableDataTimeException {
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
        logger.info("Pattern compiled");
        Matcher matcher = pattern.matcher(text);
        logger.info("Matcher created");
        if (matcher.matches()) {
            LocalDateTime dateTime = LocalDateTime.
                    parse(
                            matcher.group(1),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            logger.info("DateTime parsed successfully");

            Date date = new Date(dateTime.getYear() - 1900, dateTime.getMonthValue() - 1, dateTime.getDayOfMonth());
            Time time = new Time(dateTime.getHour(), dateTime.getMinute(), 0);
            logger.info("Placed into Date and Time");

            NotificationTask newTask = new NotificationTask(0, date, time, chatId, matcher.group(3));
            repository.save(newTask);

            return newTask;
        } else {
            throw new UnparsableDataTimeException();
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void scanForNotificationsToSend() {
        logger.info("Scanning for notifications to send");

        LocalDateTime dt = LocalDateTime.now();
        logger.info("date: " + new Date(dt.getYear() - 1900, dt.getMonthValue() - 1, dt.getDayOfMonth()) + " time: " + new Time(dt.getHour(), dt.getMinute(), 0));
        Collection<NotificationTask> collection = repository.findAllByDateAndTime(
                new Date(dt.getYear() - 1900, dt.getMonthValue() - 1, dt.getDayOfMonth()),
                new Time(dt.getHour(), dt.getMinute(), 0));
        logger.info(collection.size() + " notifications found");

        if (!collection.isEmpty()) {
            collection.stream().forEach(notification -> {
                SendMessage sendMessage = new SendMessage(notification.getChatId(), notification.getText());
                telegramBot.execute(sendMessage);
                repository.deleteById(notification.getID());
            });
        }
    }
}
