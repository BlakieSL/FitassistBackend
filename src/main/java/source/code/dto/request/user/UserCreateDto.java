package source.code.dto.request.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.model.user.ActivityLevel;
import source.code.helper.Enum.model.user.Gender;
import source.code.helper.Enum.model.user.Goal;
import source.code.validation.email.UniqueEmailDomain;
import source.code.validation.password.PasswordDigitsDomain;
import source.code.validation.password.PasswordLowercaseDomain;
import source.code.validation.password.PasswordSpecialDomain;
import source.code.validation.password.PasswordUppercaseDomain;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    @Size(max = 40)
    @NotBlank
    private String username;
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
    @NotNull
    private Gender gender;
    @NotNull
    @Past
    private LocalDate birthday;

    @Positive
    private BigDecimal height;
    @Positive
    private BigDecimal weight;
    private ActivityLevel activityLevel;
    private Goal goal;
}