package com.example.spring_server.repository;

import com.example.spring_server.entity.ExperimentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExperimentLogRepository extends JpaRepository<ExperimentLog, Long> {

    // deleted = false 인 레코드 중에서 id 로 조회
    // soft delete 된 건 조회 안 되게 막는 용도
    Optional<ExperimentLog> findByIdAndDeletedFalse(Long id);
}
