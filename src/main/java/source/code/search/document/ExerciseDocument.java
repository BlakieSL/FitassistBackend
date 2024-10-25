package source.code.search.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "exercises")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDocument {
  @Id
  private Integer id;

  @Field(type = FieldType.Text)
  private String name;
}
