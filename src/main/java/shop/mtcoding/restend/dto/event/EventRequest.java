package shop.mtcoding.restend.dto.event;

import lombok.Getter;
import shop.mtcoding.restend.model.Event.EventType;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class EventRequest {

    @Getter
    public static class EventAddDto {
        private Long id;
        private User user;
        private EventType eventType;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
    }
}
