package shop.mtcoding.restend.model.annual;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mtcoding.restend.model.user.User;

import java.util.List;

public interface AnnualRepository extends JpaRepository<Annual, Long> {
    @Override
    List<Annual> findAll();
    List<Annual> findByIdIn(List<Long> ids);

}
