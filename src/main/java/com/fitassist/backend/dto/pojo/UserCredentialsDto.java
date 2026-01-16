package com.fitassist.backend.dto.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDto implements Serializable {

	@Size(max = 50)
	@Email
	private String email;

	@Size(min = 8, max = 255)
	private String password;

	private Set<String> roles;

}
