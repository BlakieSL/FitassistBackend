package com.fitassist.backend.service.declaration.media;

import com.fitassist.backend.dto.request.media.MediaCreateDto;
import com.fitassist.backend.dto.response.other.MediaResponseDto;
import com.fitassist.backend.model.media.MediaConnectedEntity;

import java.util.List;

public interface MediaService {

	MediaResponseDto createMedia(MediaCreateDto request);

	void deleteMedia(int mediaId);

	List<MediaResponseDto> getAllMediaForParent(int parentId, MediaConnectedEntity parentType);

	MediaResponseDto getFirstMediaForParent(int parentId, MediaConnectedEntity parentType);

	MediaResponseDto getMedia(int mediaId);

}
