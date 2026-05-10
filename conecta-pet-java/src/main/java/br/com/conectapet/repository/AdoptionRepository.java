package br.com.conectapet.repository;

import br.com.conectapet.model.Adoption;
import br.com.conectapet.model.Animal;
import br.com.conectapet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    List<Adoption> findByUserOrderByCreatedAtDesc(User user);
    List<Adoption> findAllByOrderByCreatedAtDesc();

    @Query("SELECT a FROM Adoption a WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<Adoption> findByStatus(@Param("status") Adoption.AdoptionStatus status);

    boolean existsByUserAndAnimalAndStatusNot(User user, Animal animal, Adoption.AdoptionStatus status);

    long countByStatus(Adoption.AdoptionStatus status);
}
