package shop.mtcoding.restend.model.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.duty.Duty;

import shop.mtcoding.restend.model.user.User;

import java.time.LocalDate;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAll();

    List<Event> findAllByEventTypeAndUser(EventType eventType, User user);


    List<Event> findAllByUser(User user);

    Event findByAnnual_Id(Long annualId);
    Event findByDuty_Id(Long dutyId);

    Slice<Event> findByUserAndEventTypeOrderByAnnual_StartDateDesc(User user, EventType annual, Pageable page);
    Slice<Event> findByUserAndEventTypeOrderByDuty_DateDesc(User user, EventType duty, Pageable pageable);

    Slice<Event> findByEventTypeOrderByAnnual_StartDateDesc(EventType annual, Pageable page);
    Slice<Event> findByEventTypeOrderByDuty_DateDesc(EventType duty, Pageable page);

    Slice<Event> findByEventTypeAndAnnual_StartDateBetweenOrderByAnnual_StartDateDesc(EventType eventType, LocalDate start, LocalDate end, Pageable page);

    Slice<Event> findByEventTypeAndDuty_DateOrderByDuty_DateDesc(EventType duty, LocalDate start, LocalDate end, Pageable pageable);
}

