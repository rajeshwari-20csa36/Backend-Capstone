package com.ust.zynctime.repo;

import com.ust.zynctime.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByRecipients(String recipients);
    List<EmailLog> findBySentBy(String sentBy);
    List<EmailLog> findByStatus(String status);
}