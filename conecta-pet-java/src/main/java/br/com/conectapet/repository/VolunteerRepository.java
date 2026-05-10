package br.com.conectapet.repository;

import br.com.conectapet.model.User;
import br.com.conectapet.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUser(User user);
    boolean existsByUser(User user);
    List<Volunteer> findAllByOrderByCreatedAtDesc();
    long countByStatus(Volunteer.VolunteerStatus status);
    List<Volunteer> findBySkillsContaining(String skill);
}
