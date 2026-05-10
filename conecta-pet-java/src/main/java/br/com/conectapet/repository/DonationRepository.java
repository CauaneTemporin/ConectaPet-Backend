package br.com.conectapet.repository;

import br.com.conectapet.model.Donation;
import br.com.conectapet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUserOrderByCreatedAtDesc(User user);
    List<Donation> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'CONFIRMADO'")
    BigDecimal sumConfirmed();

    @Query("SELECT COUNT(DISTINCT d.donorEmail) FROM Donation d WHERE d.status = 'CONFIRMADO'")
    long countDistinctDonors();

    long countByStatus(Donation.DonationStatus status);
}
