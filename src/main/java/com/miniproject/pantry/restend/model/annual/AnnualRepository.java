package com.miniproject.pantry.restend.model.annual;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnualRepository extends JpaRepository<Annual, Long> {
    @Override
    List<Annual> findAll();
    List<Annual> findByIdIn(List<Long> ids);

    Slice<Annual> findAllByIdIn(List<Long> annualIds, Pageable page);
}
