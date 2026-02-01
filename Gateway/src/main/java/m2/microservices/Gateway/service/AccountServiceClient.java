package m2.microservices.Gateway.service;

import m2.microservices.Gateway.dto.SignUpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AccountServiceClient {
    private final RestClient restClient;

    public AccountServiceClient(@Value("${app.services.account-url}") String accountUrl) {
        this.restClient = RestClient.builder().baseUrl(accountUrl).build();
    }

    public void createProfile(String userAccessToken, SignUpRequest request) {
        // We construct the DTO expected by Account Service
        record AccountDto(String email, String firstName, String lastName, String phoneNumber) {}

        var payload = new AccountDto(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber()
        );

        restClient.post()
                .uri("/register")
                .header("Authorization", "Bearer " + userAccessToken) // Pass User Token!
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
