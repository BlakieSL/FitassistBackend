package source.code.dto.Request.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.validation.email.UniqueEmailDomain;
import source.code.validation.password.PasswordDigitsDomain;
import source.code.validation.password.PasswordLowercaseDomain;
import source.code.validation.password.PasswordSpecialDomain;
import source.code.validation.password.PasswordUppercaseDomain;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    @Size(max = 40)
    @NotBlank
    private String name;
    @Size(max = 40)
    @NotBlank
    private String surname;
    @Size(max = 50)
    @Email
    @NotBlank
    @UniqueEmailDomain
    private String email;
    @Size(min = 8, max = 255)
    @NotBlank
    @PasswordDigitsDomain
    @PasswordUppercaseDomain
    @PasswordSpecialDomain
    @PasswordLowercaseDomain
    private String password;
    @Size(min = 4, max = 6)
    @NotBlank
    private String gender;
    @NotNull
    @Past
    private LocalDate birthday;
    @NotNull
    @Positive
    private double height;
    @NotNull
    @Positive
    private double weight;
    @NotBlank
    private String activityLevel;
    @NotBlank
    private String goal;
}