package shop.mtcoding.restend.dto.event;

import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class EventResponse {
    public static class EventListDto {
        private Long id;
        private User user;
        private EventType eventType;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
    }
}
