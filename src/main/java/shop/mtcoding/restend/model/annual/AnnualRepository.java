package shop.mtcoding.restend.model.annual;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.mtcoding.restend.model.user.User;

import java.util.List;
import java.util.Optional;

public interface AnnualRepository extends JpaRepository<Annual, Long> {
    @Override
    List<Annual> findAll();
    List<Annual> findByIdIn(List<Long> ids);

    Slice<Annual> findAllByIdIn(List<Long> annualIds, Pageable page);
}
