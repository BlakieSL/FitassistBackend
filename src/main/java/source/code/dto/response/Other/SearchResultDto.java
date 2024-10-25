package source.code.dto.response.Other;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import source.code.helper.enumerators.EntityType;

@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto <T> {
  private EntityType type;
  private T data;

  public static <T> SearchResultDto<T> create(EntityType type, T data) {
    return new SearchResultDto<>(type, data);
  }
}
