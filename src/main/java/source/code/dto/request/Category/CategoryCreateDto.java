package source.code.dto.request.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDto {
  private static final int NAME_MAX_LENGTH = 50;

  @NotBlank
  @Size(max = NAME_MAX_LENGTH)
  private String name;

  @NotBlank
  private String iconUrl;

  @NotBlank
  private String gradient;
}
