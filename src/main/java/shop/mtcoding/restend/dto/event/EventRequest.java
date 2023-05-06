package shop.mtcoding.restend.dto.event;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class EventRequest {

    @Getter
    public static class EventAddInDto {
        private String eventType;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }

    @Getter
    public static class EventCancelInDto {
        private Long id;
        private String eventType;
    }

    @Getter
    public static class EventModifyInDto {
        private Long id;
        private String eventType;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }
}
