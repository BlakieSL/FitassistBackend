package com.fitassist.backend.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fitassist.backend.model.user.ActivityLevel;
import com.fitassist.backend.model.user.Gender;
import com.fitassist.backend.model.user.Goal;
import com.fitassist.backend.validation.email.UniqueEmailDomain;
import com.fitassist.backend.validation.password.PasswordDigitsDomain;
import com.fitassist.backend.validation.password.PasswordLowercaseDomain;
import com.fitassist.backend.validation.password.PasswordSpecialDomain;
import com.fitassist.backend.validation.password.PasswordUppercaseDomain;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@UniqueEmailDomain
public class UserUpdateDto {

	private Integer id;

	@Size(max = 40)
	private String username;

	@Size(max = 50)
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
	private BigDecimal height;

	@Positive
	private BigDecimal weight;

	private ActivityLevel activityLevel;

	private Goal goal;

	@PasswordDigitsDomain
	@PasswordUppercaseDomain
	@PasswordSpecialDomain
	@PasswordLowercaseDomain
	private String oldPassword;

}
