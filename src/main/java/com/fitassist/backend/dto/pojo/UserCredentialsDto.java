package com.fitassist.backend.dto.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

import static com.fitassist.backend.model.SchemaConstants.EMAIL_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDto implements Serializable {

	@Size(max = EMAIL_MAX_LENGTH)
	@Email
	private String email;

	@Size(min = 8, max = 255)
	private String password;

	private Set<String> roles;

}
