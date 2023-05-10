package shop.minostreet.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.minostreet.shoppingmall.domain.ErrorLog;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
