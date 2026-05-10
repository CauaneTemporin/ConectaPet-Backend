package br.com.conectapet.dto;

import br.com.conectapet.model.Donation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DonationDTOs {

    public record DonationRequest(
        @NotNull @DecimalMin("1.00") BigDecimal amount,
        Donation.Frequency frequency,
        Donation.PaymentMethod method,
        String donorName,
        @Email String donorEmail,
        String donorCpf
    ) {}

    public record DonationResponse(
        Long id, BigDecimal amount, String frequency, String method,
        String status, String donorName, String donorEmail, LocalDateTime createdAt
    ) {
        public static DonationResponse from(Donation d) {
            return new DonationResponse(d.getId(), d.getAmount(),
                d.getFrequency().name().toLowerCase(), d.getMethod().name().toLowerCase(),
                d.getStatus().name().toLowerCase(), d.getDonorName(), d.getDonorEmail(), d.getCreatedAt());
        }
    }

    public record DonationStatsResponse(BigDecimal total, long count, long donors) {}
}
