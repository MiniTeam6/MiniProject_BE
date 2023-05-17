package com.miniproject.pantry.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.miniproject.pantry.model.event.EventType;
import com.miniproject.pantry.model.order.OrderState;
import com.miniproject.pantry.model.user.User;
import com.miniproject.pantry.model.user.UserRole;
import lombok.Getter;
import lombok.Setter;

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
        private String imageUri;
        private String thumbnailUri;

        public LoginOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.role = user.getRole();
            this.status = user.getStatus();
            this.imageUri = user.getImageUri();
            this.thumbnailUri = user.getThumbnailUri();
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
        private Boolean status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long annualCount;

        public UserDetailOutDTO(User user) {
            this.id = user.getId();
            this.username=user.getUsername();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.imageUri = user.getImageUri();
            this.thumbnailUri = user.getThumbnailUri();
            this.role = user.getRole();
            this.status = user.getStatus();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
            this.annualCount=user.getAnnualCount();
        }
    }

    @Getter @Setter
    public static class UserRoleUpdateOutDTO{
        private String username;
        private String email;

        private UserRole role;

        public UserRoleUpdateOutDTO(User user) {
            this.username=user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRole();
        }
    }


    @JsonSerialize
    @Getter
    @Setter
    public static class UserListOutDTO{
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String role;
        private String imageUri;
        private String thumbnailUri;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;
        public UserListOutDTO(User user){
            this.id=user.getId();
            this.createAt=user.getCreatedAt();
            this.imageUri = user.getImageUri();
            this.username = user.getUsername();
            this.email=user.getEmail();
            this.phone=user.getPhone();
            this.role=user.getRole().toString();
            this.thumbnailUri = user.getThumbnailUri();
            this.updateAt=user.getUpdatedAt();
        }
    }



    @JsonSerialize
    @Getter
    @Setter
    public static class UserApprovalListOutDTO{
        private Long id;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;
        private String imageUri;
        private String username;
        private String email;
        private String role;
        public UserApprovalListOutDTO(User user){
            this.id=user.getId();
            this.createAt=user.getCreatedAt();
            this.updateAt=user.getUpdatedAt();
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
    @Getter
    @Setter
    public static class UserAnnualInfoDTO {
        private String username;
        private LocalDate startDate;
        private LocalDate endDate;
        private OrderState orderState;
        private EventType eventType;
    }

}
