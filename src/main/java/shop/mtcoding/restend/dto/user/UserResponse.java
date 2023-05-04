package shop.mtcoding.restend.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class UserResponse {

    @Getter
    public static class loginResponseDTO {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String role;
        private Boolean status;

        public loginResponseDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.role = user.getRole();
            this.status = user.getStatus();
        }
    }

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
            this.role = user.getRole().toString();
        }
    }

    @Setter
    @Getter
    public static class SignupResponseDTO {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String imageUri;
        private String thumbnailUri;

        public SignupResponseDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.imageUri = user.getImageUri();
            this.thumbnailUri = user.getThumbnailUri();
        }
    }

    @JsonSerialize
    @Getter
    @Setter
    public static class UserListOutDTO{
        private Long id;
        private LocalDateTime createAt;
        private String imageUri;
        private String username;
        private String email;
        private String role;
        public UserListOutDTO(User user){
            this.id=user.getId();
            this.createAt=user.getCreatedAt();
            this.imageUri = user.getImageUri();
            this.username = user.getUsername();
            this.email=user.getEmail();
            this.role=user.getRole().toString();
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
            this.role=user.getRole().toString();
            this.status=user.getStatus();
        }
    }
}
