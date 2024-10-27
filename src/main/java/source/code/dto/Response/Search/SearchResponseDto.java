package source.code.dto.Response.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {
  private Integer id;
  private String name;
  private String className;

  public static SearchResponseDto create(Integer id, String name, String className) {
    return new SearchResponseDto(id, name, className);
  }
}
