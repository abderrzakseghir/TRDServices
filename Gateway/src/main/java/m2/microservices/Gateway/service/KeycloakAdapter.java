package m2.microservices.Gateway.service;


import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.Gateway.dto.SignUpRequest;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdapter {
    @Value("${app.keycloak.server-url}") private String serverUrl;
    @Value("${app.keycloak.realm}") private String realm;
    @Value("${app.keycloak.admin-client-id}") private String adminClientId;
    @Value("${app.keycloak.admin-client-secret}") private String adminClientSecret;
    @Value("${app.keycloak.public-client-id}") private String publicClientId;


    // Create a new user in Keycloak AS Admin
    public void createUser(SignUpRequest request) {
        Keycloak adminClient = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .build();

        UserRepresentation  user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername((request.email()));
        user.setEmail(request.email());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.password());
        cred.setTemporary(false);

        user.setCredentials(List.of(cred));

        UsersResource usersResource = adminClient.realm(realm).users();

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == 409) {
                throw new RuntimeException("User already exists in Keycloak");
            }
            if (response.getStatus() >= 400) {
                throw new RuntimeException("Keycloak creation failed: " + response.getStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException("Keycloak creation failed by this exception : " + e.getMessage());
        }

    }


    // Login as User to get Access Token
    public AccessTokenResponse login(String email , String password){
        try(Keycloak userClient = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(publicClientId)
                .username(email)
                .password(password)
                .build())
        {
            return userClient.tokenManager().getAccessToken();
        }catch (Exception e){
            throw new RuntimeException("Invalid credentials");
        }
    }


    // DELETE for Rollback
    public void deleteUser(String email) {
        Keycloak adminClient = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .build();

        List<UserRepresentation> users = adminClient.realm(realm).users().search(email);
        if (!users.isEmpty()) {
            adminClient.realm(realm).users().get(users.get(0).getId()).remove();
            log.warn("Rolled back (deleted) user: {}", email);
        }
    }
}
