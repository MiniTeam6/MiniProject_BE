package shop.mtcoding.restend.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserResponse {

    // 회원가입 응답 (정보 확인용)
    @Getter
    public static class SignupOutDTO {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String imageUri;
        private String thumbnailUri;

        public SignupOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.imageUri = user.getImageUri();
            this.thumbnailUri = user.getThumbnailUri();
        }
    }

    // 로그인 응답
    @Getter
    public static class LoginOutDTO {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private UserRole role;
        private Boolean status;
        private LocalDate nextAnnual;
        private LocalDate nextDuty;

        public LoginOutDTO(User user, LocalDate nextAnnual, LocalDate nextDuty) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.role = user.getRole();
            this.status = user.getStatus();
            this.nextAnnual = nextAnnual;
            this.nextDuty = nextDuty;
        }
    }

    @Getter @Setter
    public static class UserDetailOutDTO{
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String imageUri;
        private String thumbnailUri;
        private UserRole role;

        public UserDetailOutDTO(User user) {
            this.id = user.getId();
            this.username=user.getUsername();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.imageUri = user.getImageUri();
            this.thumbnailUri = user.getThumbnailUri();
            this.role = user.getRole();
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
