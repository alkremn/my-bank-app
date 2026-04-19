package co.kremnev.accounts.repository;

import co.kremnev.accounts.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findBySentFalseOrderByCreatedAtAsc();
}
