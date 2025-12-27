package source.code.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.model.MediaConnectedEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDto implements Serializable {

	private Integer id;

	private String imageUrl;

	private MediaConnectedEntity parentType;

	private Integer parentId;

}
