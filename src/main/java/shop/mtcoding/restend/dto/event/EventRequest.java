package shop.mtcoding.restend.dto.event;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventRequest {

    @Getter
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
}
