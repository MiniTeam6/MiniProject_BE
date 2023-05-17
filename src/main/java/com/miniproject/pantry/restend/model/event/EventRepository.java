package com.miniproject.pantry.restend.model.event;

import com.miniproject.pantry.restend.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT e FROM Event e JOIN FETCH e.user WHERE e.eventType = :eventType AND e.annual.startDate BETWEEN :start AND :end ORDER BY e.annual.startDate DESC")
    Slice<Event> findByEventTypeAndAnnual_StartDateBetweenOrderByAnnual_StartDateDesc(EventType eventType, LocalDate start, LocalDate end, Pageable page);

    @Query("SELECT e FROM Event e JOIN FETCH e.user WHERE e.eventType = :eventType AND e.duty.date BETWEEN :start AND :end ORDER BY e.duty.date DESC")
    Slice<Event> findByEventTypeAndDuty_DateOrderByDuty_DateDesc(EventType eventType, LocalDate start, LocalDate end, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.user.id IN :userIds AND e.eventType = :eventType")
    List<Event> findEventsByUserIdsAndEventType(@Param("userIds") List<Long> userIds, @Param("eventType") EventType eventType);
}

