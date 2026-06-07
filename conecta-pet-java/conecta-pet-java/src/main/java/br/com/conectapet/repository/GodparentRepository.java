package br.com.conectapet.repository;

import br.com.conectapet.model.Animal;
import br.com.conectapet.model.Godparent;
import br.com.conectapet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GodparentRepository extends JpaRepository<Godparent, Long> {
    List<Godparent> findByUserOrderByStartedAtDesc(User user);
    List<Godparent> findAllByOrderByStartedAtDesc();
    List<Godparent> findByStatusOrderByStartedAtDesc(Godparent.GodparentStatus status);
    List<Godparent> findByAnimalAndStatusIn(Animal animal, List<Godparent.GodparentStatus> statuses);
    boolean existsByUserAndAnimalAndStatus(User user, Animal animal, Godparent.GodparentStatus status);
    long countByStatus(Godparent.GodparentStatus status);

    long countByAnimalOngIdAndStatus(Long ongId, Godparent.GodparentStatus status);
}
