package m2.microservices.bet_lifecycle_service.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PlaceBetRequest(
        @NotNull @DecimalMin("0.50") BigDecimal amountWagered,
        @NotEmpty List<SelectionRequest> selections
) {
    public record SelectionRequest(
            @NotBlank String matchId,
            @NotBlank String marketName,
            @NotBlank String selectionName,
            @NotNull @DecimalMin("1.01") BigDecimal odd
    ) {}
}