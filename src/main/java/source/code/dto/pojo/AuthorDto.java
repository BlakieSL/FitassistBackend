package source.code.dto.pojo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto implements Serializable {

	private Integer id;

	private String username;

	private String imageName;

	private String imageUrl;

}
