package shop.mtcoding.restend.model.user;

import lombok.*;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception401;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.model.event.Event;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
@Entity
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private Boolean status; // true, false

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long annualCount;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
//    @ToString.Exclude
//    private List<Event> eventList=new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setRole(UserRole role) {
        if(this.role.equals(role)){
            //checkpoint : throw 동일한 권한으로 변경할 수 없습니다.
            throw new Exception404("동일한 권한으로 변경할 수 없습니다.");
        }
        this.role=role;
    }
    public void setStatus(Boolean status){
        if(this.status.equals(status)){
        //checkpoint : throw 동일한 상태로 변경할 수 없습니다.
        throw new Exception404("이미 동일한 상태입니다.");
    }
        this.status=status;
    }

    public void verificationAnnualCount(){
        if(this.annualCount<=0){
            throw new Exception401("남은 연차개수가 없습니다.");
        }
    }

    public void setAnnualCount(Long count){
        this.annualCount-=count;
    }


    @Builder
    public User(Long id, String username, String password, String email, String phone, String imageUri, String thumbnailUri, String role, Boolean status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.imageUri = imageUri;
        this.thumbnailUri = thumbnailUri;
        this.role = UserRole.valueOf(role);
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.annualCount=15L;
    }

    public void update(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.imageUri = user.getImageUri();
        this.thumbnailUri = user.getThumbnailUri();
        this.role = user.getRole();
        this.status = user.getStatus();
    }
}