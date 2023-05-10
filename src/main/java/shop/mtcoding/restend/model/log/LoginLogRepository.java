package shop.minostreet.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.minostreet.shoppingmall.domain.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
