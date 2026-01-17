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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	public static UserCreateDto of(String username, String email, String password, Gender gender, LocalDate birthday) {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setUsername(username);
		userCreateDto.setEmail(email);
		userCreateDto.setPassword(password);
		userCreateDto.setGender(gender);
		userCreateDto.setBirthday(birthday);
		return userCreateDto;
	}

}
