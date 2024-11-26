package source.code.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import source.code.helper.Enum.model.user.ActivityLevelType;
import source.code.helper.Enum.model.user.GenderType;
import source.code.helper.Enum.model.user.GoalType;
import source.code.validation.email.UniqueEmailDomain;
import source.code.validation.password.PasswordDigitsDomain;
import source.code.validation.password.PasswordLowercaseDomain;
import source.code.validation.password.PasswordSpecialDomain;
import source.code.validation.password.PasswordUppercaseDomain;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateDto {
    @Size(max = 40)
    private String name;

    @Size(max = 40)
    private String surname;

    @Size(max = 50)
    @Email
    @UniqueEmailDomain
    private String email;

    @Size(min = 8, max = 255)
    @PasswordDigitsDomain
    @PasswordUppercaseDomain
    @PasswordSpecialDomain
    @PasswordLowercaseDomain
    private String password;

    @Size(min = 4, max = 6)
    private GenderType gender;

    @Past
    private LocalDate birthday;

    @Positive
    private double height;

    @Positive
    private double weight;

    private ActivityLevelType activityLevel;

    private GoalType goal;

    @PasswordDigitsDomain
    @PasswordUppercaseDomain
    @PasswordSpecialDomain
    @PasswordLowercaseDomain
    private String oldPassword;
}