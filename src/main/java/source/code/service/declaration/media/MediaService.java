package source.code.service.declaration.media;

import java.util.List;

import source.code.dto.request.media.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.helper.Enum.model.MediaConnectedEntity;

public interface MediaService {

	MediaResponseDto createMedia(MediaCreateDto request);

	void deleteMedia(int mediaId);

	List<MediaResponseDto> getAllMediaForParent(int parentId, MediaConnectedEntity parentType);

	MediaResponseDto getFirstMediaForParent(int parentId, MediaConnectedEntity parentType);

	MediaResponseDto getMedia(int mediaId);

}
