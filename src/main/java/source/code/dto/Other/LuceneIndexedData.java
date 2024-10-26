package source.code.dto.Other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LuceneIndexedData {
  private String id;
  private String name;

  public static LuceneIndexedData create(String id, String name) {
    return new LuceneIndexedData(id, name);
  }
}
