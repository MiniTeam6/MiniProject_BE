package shop.mtcoding.restend.model.event;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mtcoding.restend.model.user.User;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAll();
    List<Event> findAllByEventTypeAndUser(EventType eventType, User user);


}

