package com.miniproject.pantry.dto.event;

import com.miniproject.pantry.model.annual.Annual;
import com.miniproject.pantry.model.duty.Duty;
import com.miniproject.pantry.model.event.Event;
import com.miniproject.pantry.model.event.EventType;
import com.miniproject.pantry.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;


public class EventRequest {

    @Getter
    @Setter
    public static class EventAddInDto {
        @NotEmpty
        @Pattern(regexp = "ANNUAL|DUTY")
        private String eventType;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Long count;
    }

    @Getter
    @Setter
    public static class EventAddDto {
        @NotEmpty
        @Pattern(regexp = "DUTY|ANNUAL")
        private String eventType;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        public Event annualToEventEntity(User user, Annual annual){
            return Event.builder().
                    eventType(EventType.ANNUAL).
                    annual(annual).
                    user(user).build();
        }

        public Event dutyToEventEntity(User user, Duty duty){
            return Event.builder().
                    eventType(EventType.DUTY).
                    duty(duty).
                    user(user).build();
        }
        public Annual annualToEntity(){
            return Annual.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
        }
        public Duty dutyToEntity(){
            return Duty.builder().date(date).build();
        }

    }

    @Getter
    @Setter
    public static class EventCancelInDto {
        @NotNull
        private Long eventId;
        @NotEmpty
        @Pattern(regexp = "ANNUAL|DUTY")
        private String eventType;
    }

    @Getter
    @Setter
    public static class EventModifyInDto {
        @NotNull
        private Long eventId;
        @NotEmpty
        @Pattern(regexp = "ANNUAL|DUTY")
        private String eventType;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        @NotNull
        private Long count;
    }
}