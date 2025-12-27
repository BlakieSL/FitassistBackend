package source.code.dto.response.search;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SearchResponseDto implements Serializable {

	private Integer id;

	private String name;

	private String type;

}
