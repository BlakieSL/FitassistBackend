package source.code.dto.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaImagesDto implements Serializable {

	private List<String> imageNames = new ArrayList<>();

	private List<String> imageUrls = new ArrayList<>();

}
