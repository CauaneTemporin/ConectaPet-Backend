package br.com.conectapet.repository;

import br.com.conectapet.model.Occurrence;
import br.com.conectapet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {
    List<Occurrence> findAllByOrderByCreatedAtDesc();
    List<Occurrence> findByStatusOrderByCreatedAtDesc(Occurrence.OccurrenceStatus status);
    List<Occurrence> findByUserOrderByCreatedAtDesc(User user);
    long countByStatus(Occurrence.OccurrenceStatus status);
}
