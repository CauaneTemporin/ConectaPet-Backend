package br.com.conectapet.repository;

import br.com.conectapet.model.Denuncia;
import br.com.conectapet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    List<Denuncia> findByUserOrderByCreatedAtDesc(User user);
    List<Denuncia> findAllByOrderByCreatedAtDesc();
    List<Denuncia> findByStatusOrderByCreatedAtDesc(Denuncia.StatusDenuncia status);
    long countByStatus(Denuncia.StatusDenuncia status);

    List<Denuncia> findByOngIdOrderByCreatedAtDesc(Long ongId);
    List<Denuncia> findByOngIdAndStatusOrderByCreatedAtDesc(Long ongId, Denuncia.StatusDenuncia status);
}
