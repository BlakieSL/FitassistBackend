package source.code.dto.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaImagesDto implements Serializable {

	private List<String> imageNames = new ArrayList<>();

	private List<String> imageUrls = new ArrayList<>();

}
