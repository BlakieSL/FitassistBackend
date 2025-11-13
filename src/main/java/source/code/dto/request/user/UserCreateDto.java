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
    private Gender gender;
    @Past
    private LocalDate birthday;

    @Positive
    private BigDecimal height;
    @Positive
    private BigDecimal weight;
    private ActivityLevel activityLevel;
    private Goal goal;

    public static UserCreateDto of(
            String username,
            String email,
            String password,
            Gender gender,
            LocalDate birthday) {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(username);
        userCreateDto.setEmail(email);
        userCreateDto.setPassword(password);
        userCreateDto.setGender(gender);
        userCreateDto.setBirthday(birthday);
        return userCreateDto;
    }
}