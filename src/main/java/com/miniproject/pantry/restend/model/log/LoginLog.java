package com.miniproject.pantry.restend.model.log;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

//Audit 기능 사용하기 위한 어노테이션 1
@EntityListeners(AuditingEntityListener.class)
//Spring이 User 객체 생성시 빈생성자로 생성하기 때문에
@NoArgsConstructor
//@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "login_log_tb")
@Entity
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String userAgent;
    private String clientIP;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;


    @Builder
    public LoginLog(Long id, Long userId, String userAgent, String clientIP, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userAgent = userAgent;
        this.clientIP = clientIP;
        this.createdAt = createdAt;
    }
}
