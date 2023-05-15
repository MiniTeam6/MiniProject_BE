package shop.mtcoding.restend.model.duty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface DutyRepository extends JpaRepository<Duty, Long> {
    @Override
    List<Duty> findAll();

    List<Duty> findByIdIn(List<Long> ids);

}
