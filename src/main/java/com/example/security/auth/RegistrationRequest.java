package com.example.security.auth;


import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

@Getter
@Setter
@Builder
public class    RegistrationRequest {


    @NotEmpty(message = "Firstname is mandatory")
    @NotNull(message = "Firstname is mandatory")
    private String firstname;
    @NotEmpty(message = "Lastname is mandatory")
    @NotNull(message = "Lastname is mandatory")
    private String lastname;
    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotNull(message = "Email is mandatory")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    @NotNull(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    private String password;

    @NotNull(message = "Department is required for admin", groups = AdminValidation.class)
    @Size(min = 2, message = "Department must be at least 2 characters", groups = AdminValidation.class)
    private String adminDepartment;

    @NotNull(message = "Security level is required for admin", groups = AdminValidation.class)
    private String adminSecurityLevel;

    @NotNull(message = "Company is required for client", groups = ClientValidation.class)
    @Size(min = 2, message = "Company must be at least 2 characters", groups = ClientValidation.class)
    private String clientCompany;

    @NotNull(message = "Subscription type is required for client", groups = ClientValidation.class)
    private String clientSubscriptionType;



    private boolean isAdmin;
    private boolean  isClient;

    @AssertTrue(message = "Exactly one role must be true")
    private boolean isExactlyOneRole() {
        return isAdmin ^ isClient;
    }

    public interface AdminValidation {}
    public interface ClientValidation {}

    public void validateRoleSpecificFields() {
        if (isAdmin) {
            Validate.notNull(adminDepartment, "Department is required for admin");
            Validate.notNull(adminSecurityLevel, "Security level is required for admin");
        } else if (isClient) {
            Validate.notNull(clientCompany, "Company is required for client");
            Validate.notNull(clientSubscriptionType, "Subscription type is required for client");
        }
    }

}
