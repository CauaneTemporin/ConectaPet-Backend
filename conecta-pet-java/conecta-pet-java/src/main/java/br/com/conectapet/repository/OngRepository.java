package br.com.conectapet.repository;

import br.com.conectapet.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OngRepository extends JpaRepository<Ong, Long> {

    List<Ong> findByStatusOrderByNomeFantasiaAsc(Ong.OngStatus status);

    List<Ong> findAllByOrderByCreatedAtDesc();

    boolean existsByCnpj(String cnpj);

    Optional<Ong> findByCnpj(String cnpj);

    long countByStatus(Ong.OngStatus status);
}
