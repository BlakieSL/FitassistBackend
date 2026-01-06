package source.code.mapper.helper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import source.code.dto.pojo.AuthorDto;
import source.code.dto.pojo.MediaImagesDto;
import source.code.dto.request.text.TextUpdateDto;
import source.code.model.media.Media;
import source.code.model.text.TextBase;
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

	public <T extends TextBase> void updateTextAssociations(Set<T> existingItems, List<TextUpdateDto> newItems,
			Function<TextUpdateDto, T> creator) {

		List<Integer> updatedIds = newItems.stream().map(TextUpdateDto::getId).filter(Objects::nonNull).toList();
		existingItems.removeIf(item -> !updatedIds.contains(item.getId()));

		for (TextUpdateDto dto : newItems) {
			if (dto.getId() != null) {
				existingItems.stream().filter(item -> item.getId().equals(dto.getId())).findFirst().ifPresent(item -> {
					if (dto.getOrderIndex() != null) {
						item.setOrderIndex(dto.getOrderIndex());
					}
					if (dto.getText() != null) {
						item.setText(dto.getText());
					}
					if (dto.getTitle() != null) {
						item.setTitle(dto.getTitle());
					}
				});
			}
			else {
				T newItem = creator.apply(dto);
				existingItems.add(newItem);
			}
		}
	}

}
