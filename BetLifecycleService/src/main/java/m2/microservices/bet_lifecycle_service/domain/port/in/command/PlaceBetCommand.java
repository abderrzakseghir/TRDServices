package m2.microservices.bet_lifecycle_service.domain.port.in.command;

import java.math.BigDecimal;
import java.util.List;

public record PlaceBetCommand(
        String accountId,
        BigDecimal amountWagered,
        List<SelectionCommand> selections
) {
    public record SelectionCommand(
            String matchId,
            String marketName,
            String selectionName,
            BigDecimal odd
    ) {
    }
}
