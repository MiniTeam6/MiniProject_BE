package com.miniproject.pantry.restend.model.log;

import org.springframework.data.jpa.repository.JpaRepository;
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
