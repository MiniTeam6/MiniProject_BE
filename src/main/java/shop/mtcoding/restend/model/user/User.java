package shop.mtcoding.restend.model.user;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
@Entity
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 60) // 패스워드 인코딩(BCrypt)
    private String password;

    @Column(unique = true, nullable = false, length = 20)
    private String email;

    @Column(nullable = false, length = 13)
    private String phone;

    private String imageUri; //이미지 경로

    private String thumbnailUri; //썸네일 경로

    private String role;

    private Boolean status; // true, false

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setStatus(Boolean status){this.status=status;}

    @Builder
    public User(Long id, String username, String password, String email, String phone, String imageUri, String thumbnailUri, String role, Boolean status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.imageUri = imageUri;
        this.thumbnailUri = thumbnailUri;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}