package shop.mtcoding.restend.dto.user;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

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

        public JoinOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
    }

    public static class UserListOutDTO{
        private String image;
        private String username;
        private String email;
        private String role;
        public UserListOutDTO(User user){
            this.image = user.getImage();
            this.username = user.getUsername();
            this.email=user.getEmail();
            this.role=user.getRole();
        }
    }
}