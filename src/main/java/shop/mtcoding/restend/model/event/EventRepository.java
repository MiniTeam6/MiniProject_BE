package shop.mtcoding.restend.model.event;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.user.User;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAll();

    List<Event> findAllByUser(User user);

    Event findByAnnual_Id(Long annualId);
    Event findByDuty_Id(Long dutyId);
}

