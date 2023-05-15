package shop.mtcoding.restend.model.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.minostreet.shoppingmall.domain.ErrorLog;
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
