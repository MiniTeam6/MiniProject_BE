package shop.mtcoding.restend.dto.event;

import lombok.Builder;
import lombok.Getter;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventResponse {

    @Getter
    @Builder
    public static class EventAddOutDTO {
        private Long eventId;
        private Long userId;
        private EventType eventType;
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class EventModifyOutDTO {
        private EventType eventType;
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    public static class EventListOutDTO {
        private Long eventId;
        private Long userId;
        private String userUsername;
        private String userEmail;
        private String userImageUri;
        private String userThumbnailUri;
        private EventType eventType;
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String orderOrderState;
    }
}
