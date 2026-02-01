package m2.microservices.AccountService.adapter.in.web.dto;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber
) {}
