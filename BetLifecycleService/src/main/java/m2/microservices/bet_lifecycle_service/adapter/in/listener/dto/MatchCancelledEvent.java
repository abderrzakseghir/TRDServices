package m2.microservices.bet_lifecycle_service.adapter.in.listener.dto;

public record MatchCancelledEvent(
        String matchId,
        String reason
) {
}
