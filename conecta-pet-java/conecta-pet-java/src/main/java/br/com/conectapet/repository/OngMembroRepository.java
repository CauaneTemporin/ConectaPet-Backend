package br.com.conectapet.repository;

import br.com.conectapet.model.Ong;
import br.com.conectapet.model.OngMembro;
import br.com.conectapet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OngMembroRepository extends JpaRepository<OngMembro, Long> {

    List<OngMembro> findByOngOrderByJoinedAtDesc(Ong ong);

    List<OngMembro> findByUserOrderByJoinedAtDesc(User user);

    Optional<OngMembro> findByOngAndUser(Ong ong, User user);

    boolean existsByOngAndUser(Ong ong, User user);

    boolean existsByOngAndUserAndRole(Ong ong, User user, OngMembro.OngMembroRole role);

    List<OngMembro> findByOngAndStatusOrderByJoinedAtDesc(Ong ong, OngMembro.OngMembroStatus status);

    long countByOng(Ong ong);
}
