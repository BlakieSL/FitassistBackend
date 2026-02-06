package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.pojo.MediaImagesDto;
import com.fitassist.backend.dto.request.text.TextUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.CategoryEntity;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.text.TextBase;
import com.fitassist.backend.model.user.User;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Component
public class CommonMappingHelper {

	@Named("mapMediaToFirstImageName")
	public String mapMediaToFirstImageName(List<Media> mediaList) {
		return mediaList.stream().findFirst().map(Media::getImageName).orElse(null);
	}

	@Named("mapMediaListToImagesDto")
	public MediaImagesDto mapMediaListToImagesDto(List<Media> mediaList) {
		List<String> imageNames = mediaList.stream().map(Media::getImageName).toList();

		return MediaImagesDto.ofNames(imageNames);
	}

	@Named("mapUserToAuthorDto")
	public AuthorDto mapUserToAuthorDto(User user) {
		return AuthorDto.withoutImage(user.getId(), user.getUsername());
	}

	@Named("mapCategoryToResponse")
	public CategoryResponseDto mapCategoryToResponse(CategoryEntity category) {
		return Optional.ofNullable(category)
			.map(c -> new CategoryResponseDto(c.getId(), c.getName()))
			.orElse(null);
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
