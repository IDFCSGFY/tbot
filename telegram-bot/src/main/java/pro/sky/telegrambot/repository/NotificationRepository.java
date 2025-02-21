package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.sql.Date;
import java.sql.Time;
import java.util.Collection;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationTask, Long> {

    Collection<NotificationTask> findAllByDateAndTime(Date date, Time time);

}
