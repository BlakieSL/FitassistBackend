package source.code.mapper.helper;

import java.util.List;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import source.code.dto.pojo.AuthorDto;
import source.code.dto.pojo.MediaImagesDto;
import source.code.model.media.Media;
import source.code.model.user.User;

@Component
public class CommonMappingHelper {

	@Named("mapMediaToFirstImageName")
	public String mapMediaToFirstImageName(List<Media> mediaList) {
		if (mediaList.isEmpty())
			return null;
		return mediaList.getFirst().getImageName();
	}

	@Named("mapMediaListToImagesDto")
	public MediaImagesDto mapMediaListToImagesDto(List<Media> mediaList) {
		var dto = new MediaImagesDto();
		var imageNames = mediaList.stream().map(Media::getImageName).toList();

		dto.setImageNames(imageNames);

		return dto;
	}

	@Named("userToAuthorDto")
	public AuthorDto userToAuthorDto(User user) {
		var authorDto = new AuthorDto();
		authorDto.setId(user.getId());
		authorDto.setUsername(user.getUsername());
		return authorDto;
	}

}
