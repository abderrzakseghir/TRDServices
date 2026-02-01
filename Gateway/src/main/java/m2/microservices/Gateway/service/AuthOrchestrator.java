package m2.microservices.Gateway.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.Gateway.dto.AuthResponse;
import m2.microservices.Gateway.dto.LoginRequest;
import m2.microservices.Gateway.dto.SignUpRequest;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthOrchestrator {
    private final KeycloakAdapter keycloakAdapter;
    private final AccountServiceClient accountClient;

    public AuthResponse login(LoginRequest request) {
        AccessTokenResponse t = keycloakAdapter.login(request.email(), request.password());
        return new AuthResponse(t.getToken(), t.getRefreshToken(), t.getExpiresIn());
    }

    public AuthResponse signUp(SignUpRequest request) {
        log.info("Orchestrating sign-up for: {}", request.email());

        // 1. Create Identity
        keycloakAdapter.createUser(request);

        // 2. Get Token (Login)
        AccessTokenResponse tokens;
        try {
            tokens = keycloakAdapter.login(request.email(), request.password());
        } catch (Exception e) {
            // If we can't login immediately after creating, something is wrong. Rollback.
            keycloakAdapter.deleteUser(request.email());
            throw new RuntimeException("Failed to login after creation");
        }

        // 3. Create Business Profile
        try {
            accountClient.createProfile(tokens.getToken(), request);
        } catch (Exception e) {
            log.error("Account Service failed. Rolling back Keycloak user.", e);
            // CRITICAL: Delete the auth user so we don't have a ghost account
            keycloakAdapter.deleteUser(request.email());
            throw new RuntimeException("Failed to create profile. Please try again.");
        }

        // 4. Return Token
        return new AuthResponse(tokens.getToken(), tokens.getRefreshToken(), tokens.getExpiresIn());
    }
}
