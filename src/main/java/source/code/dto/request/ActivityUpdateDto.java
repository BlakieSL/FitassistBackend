package source.code.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActivityUpdateDto {
  private static final int NAME_MAX_LENGTH = 50;

  @Size(max = NAME_MAX_LENGTH)
  private String name;

  @Positive
  private Double met;

  private Integer categoryId;
}
