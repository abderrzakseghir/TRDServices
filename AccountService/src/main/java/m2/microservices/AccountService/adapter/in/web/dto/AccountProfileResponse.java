package m2.microservices.AccountService.adapter.in.web.dto;

import java.util.List;

public record AccountProfileResponse (
        String accountId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        List<BetHistoryResponse> betHistories
){}
