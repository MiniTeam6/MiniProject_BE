package shop.mtcoding.restend.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class UserResponse {
    @Getter @Setter
    public static class DetailOutDTO{
        private Long id;
        private String username;
        private String email;

        private String role;

        public DetailOutDTO(User user) {
            this.id = user.getId();
            this.username=user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRole();
        }
    }

    @Setter
    @Getter
    public static class JoinOutDTO {
        private Long id;
        private String username;
        private String email;

        public JoinOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }

    @JsonSerialize
    @Getter
    @Setter
    public static class UserListOutDTO{
        private Long id;
        private LocalDateTime createAt;
        private String image;
        private String username;
        private String email;
        private String role;
        public UserListOutDTO(User user){
            this.id=user.getId();
            this.createAt=user.getCreatedAt();
            this.image = user.getImage();
            this.username = user.getUsername();
            this.email=user.getEmail();
            this.role=user.getRole();
        }
    }
    @Getter @Setter
    public static class StatusUpdateOutDTO{
        private Long id;
        private String username;
        private String email;
        private String role;
        private Boolean status;

        public StatusUpdateOutDTO(User user){
            this.id= user.getId();
            this.username=user.getUsername();
            this.email=user.getEmail();
            this.role=user.getRole();
            this.status=user.getStatus();
        }
    }
}
