package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.UnparsableDataTimeException;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskService service;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if(update.message().text().equals("/start")){
                SendMessage sendMessage = new SendMessage(update.message().chat().id(), "The bot was started");
                telegramBot.execute(sendMessage);
            } else if (!update.message().text().isEmpty()){
                SendMessage sendMessage = null;
                try {
                    sendMessage = new SendMessage(update.message().chat().id(), service.newNotification(update.message().text(), update.message().chat().id()).toString());
                } catch (UnparsableDataTimeException e) {
                    StringBuilder rowIfNeeded = new StringBuilder();
                    if(!(e.getMessage() == null)){
                        rowIfNeeded.append(" in ");
                        rowIfNeeded.append(e.getMessage());
                    }
                    sendMessage = new SendMessage(update.message().chat().id(),
                            """
                                **Oops!** Seems like I'm having troubles reading your date format%s. \r
                                Please, try using the following format: \r
                                **dd.mm.yyyy hh:mm** <here is your notification text!>
                                """.formatted(rowIfNeeded));
                }
                telegramBot.execute(sendMessage);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
