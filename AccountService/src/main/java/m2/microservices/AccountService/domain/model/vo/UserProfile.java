package m2.microservices.AccountService.domain.model.vo;


public record UserProfile(
    String email,
    String firstName,
    String lastName,
    String phoneNumber
) {
    public UserProfile {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }

        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

    }

    public String userName() {
        return firstName + (lastName != null && !lastName.isBlank() ? " " + lastName : "");
    }


}
