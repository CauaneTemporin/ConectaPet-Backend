package br.com.conectapet.service;

import br.com.conectapet.dto.DonationDTOs;
import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository     userRepository;

    @Transactional
    public DonationDTOs.DonationResponse donate(DonationDTOs.DonationRequest req, String userEmail) {
        User user = userEmail != null ? userRepository.findByEmail(userEmail).orElse(null) : null;
        var donation = Donation.builder()
            .user(user).amount(req.amount())
            .frequency(req.frequency() != null ? req.frequency() : Donation.Frequency.UNICO)
            .method(req.method() != null ? req.method() : Donation.PaymentMethod.PIX)
            .donorName(req.donorName()).donorEmail(req.donorEmail()).donorCpf(req.donorCpf())
            .status(Donation.DonationStatus.CONFIRMADO)
            .build();
        log.info("Doação confirmada: R$ {} de {}", req.amount(), req.donorEmail());
        return DonationDTOs.DonationResponse.from(donationRepository.save(donation));
    }

    public List<DonationDTOs.DonationResponse> myDonations(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return donationRepository.findByUserOrderByCreatedAtDesc(user)
            .stream().map(DonationDTOs.DonationResponse::from).toList();
    }

    public List<DonationDTOs.DonationResponse> listAll() {
        return donationRepository.findAllByOrderByCreatedAtDesc()
            .stream().map(DonationDTOs.DonationResponse::from).toList();
    }

    public DonationDTOs.DonationStatsResponse stats() {
        BigDecimal total = donationRepository.sumConfirmed();
        return new DonationDTOs.DonationStatsResponse(
            total != null ? total : BigDecimal.ZERO,
            donationRepository.countByStatus(Donation.DonationStatus.CONFIRMADO),
            donationRepository.countDistinctDonors());
    }
}
