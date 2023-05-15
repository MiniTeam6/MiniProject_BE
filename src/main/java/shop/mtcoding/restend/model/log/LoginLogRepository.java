package shop.mtcoding.restend.model.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.minostreet.shoppingmall.domain.LoginLog;
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
