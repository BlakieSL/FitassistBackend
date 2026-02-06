package com.fitassist.backend.dto.request.user;

import com.fitassist.backend.model.user.ActivityLevel;
import com.fitassist.backend.model.user.Gender;
import com.fitassist.backend.model.user.Goal;
import com.fitassist.backend.validation.email.UniqueEmailDomain;
import com.fitassist.backend.validation.password.PasswordDigitsDomain;
import com.fitassist.backend.validation.password.PasswordLowercaseDomain;
import com.fitassist.backend.validation.password.PasswordSpecialDomain;
import com.fitassist.backend.validation.password.PasswordUppercaseDomain;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.fitassist.backend.model.SchemaConstants.EMAIL_MAX_LENGTH;
import static com.fitassist.backend.model.SchemaConstants.USERNAME_MAX_LENGTH;

@Getter
@Setter
@UniqueEmailDomain
public class UserUpdateDto {

	private Integer id;

	@Size(max = USERNAME_MAX_LENGTH)
	private String username;

	@Size(max = EMAIL_MAX_LENGTH)
	@Email
	private String email;

	@Size(min = 8, max = 255)
	@PasswordDigitsDomain
	@PasswordUppercaseDomain
	@PasswordSpecialDomain
	@PasswordLowercaseDomain
	private String password;

	private Gender gender;

	@Past
	private LocalDate birthday;

	@Positive
	@Digits(integer = 3, fraction = 1)
	@Min(50)
	@Max(300)
	private BigDecimal height;

	@Positive
	@Digits(integer = 3, fraction = 1)
	@Min(20)
	@Max(500)
	private BigDecimal weight;

	private ActivityLevel activityLevel;

	private Goal goal;

	@PasswordDigitsDomain
	@PasswordUppercaseDomain
	@PasswordSpecialDomain
	@PasswordLowercaseDomain
	private String oldPassword;

}
