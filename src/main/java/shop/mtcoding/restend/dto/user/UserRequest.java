package shop.mtcoding.restend.dto.user;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserRequest {
    @Setter
    @Getter
    public static class LoginRequestDTO {
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty
        private String email;

        @NotEmpty
        @Size(min = 8, max = 20)
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,20}$", message = "영문/숫자/특수문자를 조합하여 8~20자 이내로 작성해주세요")
        private String password;
    }

    @Setter
    @Getter
    public static class SignupRequestDTO {
//        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요")
//        @Pattern(regexp = "^[가-힣]{2,4}[\\s]?[가-힣]{2,5}$", message = "올바른 이름 형식으로 작성해주세요.")   //이름 정규표현식
        @Pattern(regexp = "^[가-힣]{1}[가-힣]{1,3}$", message = "올바른 이름 형식으로 작성해주세요.")
        @NotEmpty
        private String username;

        @NotEmpty
        @Size(min = 8, max = 20)
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,20}$", message = "영문/숫자/특수문자를 조합하여 8~20자 이내로 작성해주세요")
        private String password;

        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^(010|011|016|017|018|019)-\\d{3,4}-\\d{4}$", message = "올바른 휴대폰 번호 형식으로 작성해주세요")
        private String phone;


        public User toEntity(String imageUri, String thumbnailUri) {
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .phone(phone)
                    .imageUri(imageUri)
                    .thumbnailUri(thumbnailUri)
                    .role("USER")
                    .status(true)
                    .build();
        }
    }
}
