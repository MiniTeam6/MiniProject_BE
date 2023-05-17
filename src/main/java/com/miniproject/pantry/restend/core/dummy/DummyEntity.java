package com.miniproject.pantry.restend.core.dummy;

import com.miniproject.pantry.restend.model.annual.Annual;
import com.miniproject.pantry.restend.model.duty.Duty;
import com.miniproject.pantry.restend.model.event.Event;
import com.miniproject.pantry.restend.model.event.EventType;
import com.miniproject.pantry.restend.model.order.Order;
import com.miniproject.pantry.restend.model.order.OrderState;
import com.miniproject.pantry.restend.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username, String role, Boolean status) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String email = "";
        if (username.equals("사르")) {
            email = "ssar";
        } else if (username.equals("러브")) {
            email = "love";
        } else if (username.equals("코스")){
            email = "cos";
        } else if(username.equals("코코")){
            email = "coco";
        }
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("aaaa1234@@"))
                .email(email + "@nate.com")
                .imageUri("https://test")
                .thumbnailUri("https://test")
                .phone("010-1234-1234")
                .role(role)
                .status(status)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User newMockUser(Long id, String username, String role) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String email = "";
        if (username.equals("사르")) {
            email = "ssar";
        } else if (username.equals("러브")) {
            email = "love";
        } else if (username.equals("코스")) {
            email = "cos";
        } else if (username.equals("코코")) {
            email = "coco";
        }
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("aaaa1234@@"))
                .email(email + "@nate.com")
                .imageUri("https://test")
                .thumbnailUri("https://test")
                .phone("010-1234-1234")
                .role(role)
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public Event newEvent(User user, String eventType, Annual annual, Duty duty) {
        return Event.builder()
                .user(user)
                .eventType(EventType.valueOf(eventType))
                .annual(annual)
                .duty(duty)
                .build();
    }

    public Event newMockEvent(Long id, User user, String eventType, Annual annual, Duty duty) {
        return Event.builder()
                .id(id)
                .user(user)
                .annual(annual)
                .duty(duty)
                .eventType(EventType.valueOf(eventType))
                .build();
    }

    public Annual newAnnual(LocalDate startDate, LocalDate endDate, Long count) {
        return Annual.builder()
                .startDate(startDate)
                .endDate(endDate)
                .count(count)
                .build();
    }

    protected Annual newMockAnnual(Long id, LocalDate startDate, LocalDate endDate, Long count) {
        return Annual.builder()
                .id(id)
                .startDate(startDate)
                .endDate(endDate)
                .count(count)
                .build();
    }

    public Duty newDuty(LocalDate date) {
        return Duty.builder()
                .date(date)
                .build();
    }

    protected Duty newMockDuty(Long id, LocalDate date) {
        return Duty.builder()
                .id(id)
                .date(date)
                .build();
    }
//    public Order(Long id, Event event, OrderState orderState, User approver, LocalDateTime createdAt) {
        public Order newOrder(Event event, OrderState orderState, User approver) {
        return Order.builder()
                .event(event)
                .orderState(orderState)
                .approver(approver)
                .build();
    }

    public Order newMockOrder(Long id,Event event, OrderState orderState, User approver) {
        return Order.builder()
                .id(id)
                .event(event)
                .orderState(orderState)
                .approver(approver)
                .build();
    }
}
