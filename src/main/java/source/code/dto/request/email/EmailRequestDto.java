package source.code.dto.request.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailRequestDto {

    @NotBlank
    @Email
    private String fromEmail;

    @NotEmpty
    private List<@Email String> toEmails;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    private boolean isHtml = false;
}

