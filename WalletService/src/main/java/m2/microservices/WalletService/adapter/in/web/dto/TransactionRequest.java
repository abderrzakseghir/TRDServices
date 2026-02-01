package m2.microservices.WalletService.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String paymentReference
) {}