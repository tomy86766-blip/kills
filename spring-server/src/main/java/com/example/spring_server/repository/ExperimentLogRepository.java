package com.example.spring_server.repository;

import com.example.spring_server.entity.ExperimentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperimentLogRepository extends JpaRepository<ExperimentLog, Long> {

    Optional<ExperimentLog> findByIdAndDeletedFalse(Long id);

    List<ExperimentLog> findAllByDeletedFalse();
}
