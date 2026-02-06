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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		return Optional.ofNullable(category).map(c -> new CategoryResponseDto(c.getId(), c.getName())).orElse(null);
	}

	public <T extends TextBase> void updateTextAssociations(Set<T> existingItems, List<TextUpdateDto> newItems,
			Function<TextUpdateDto, T> creator) {
		Map<Integer, T> existingItemsMap = existingItems.stream()
			.collect(Collectors.toMap(T::getId, Function.identity()));

		List<T> updatedItems = newItems.stream().map(dto -> {
			if (shouldUpdate(dto, existingItemsMap)) {
				T existingItem = existingItemsMap.get(dto.getId());
				Optional.ofNullable(dto.getOrderIndex()).ifPresent(existingItem::setOrderIndex);
				Optional.ofNullable(dto.getText()).ifPresent(existingItem::setText);
				Optional.ofNullable(dto.getTitle()).ifPresent(existingItem::setTitle);
				return existingItem;
			}
			else {
				return creator.apply(dto);
			}
		}).toList();

		existingItems.clear();
		existingItems.addAll(updatedItems);
	}

	private <T extends TextBase> boolean shouldUpdate(TextUpdateDto dto, Map<Integer, T> existingMap) {
		return dto.getId() != null && existingMap.containsKey(dto.getId());
	}

}
