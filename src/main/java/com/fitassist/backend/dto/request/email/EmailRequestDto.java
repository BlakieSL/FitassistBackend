package com.fitassist.backend.dto.request.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailRequestDto {

	@NotBlank
	@Email
	private String fromEmail;

	@NotBlank
	@Email
	private String toEmail;

	@NotBlank
	private String subject;

	@NotBlank
	private String content;

}
