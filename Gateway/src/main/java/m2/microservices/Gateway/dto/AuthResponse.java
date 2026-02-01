package m2.microservices.Gateway.dto;

public record AuthResponse (
        String accessToken,
        String refreshToken,
        long expiresIn
){
}
