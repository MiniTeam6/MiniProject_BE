package com.miniproject.pantry.restend.model.log;

import org.springframework.data.jpa.repository.JpaRepository;
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
