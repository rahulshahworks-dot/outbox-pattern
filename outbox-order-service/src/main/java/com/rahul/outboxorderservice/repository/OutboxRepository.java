package com.rahul.outboxorderservice.repository;

import com.rahul.outboxorderservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    // Cluster-Safe: Jo row ek worker ne lock ki, doosra usko SKIP kar dega
    @Query(value = "SELECT * FROM outbox_events WHERE status = :status ORDER BY created_at ASC LIMIT 10 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findPendingEventsWithLock(@Param("status") String status);
}
