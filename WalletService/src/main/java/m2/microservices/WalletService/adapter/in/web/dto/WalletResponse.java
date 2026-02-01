package m2.microservices.WalletService.adapter.in.web.dto;

import java.math.BigDecimal;

public record WalletResponse(
        String accountId,
        BigDecimal balance,
        String currency
) {}