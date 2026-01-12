package source.code.service.declaration.media;

import source.code.dto.request.media.MediaCreateDto;
import source.code.dto.response.other.MediaResponseDto;
import source.code.helper.Enum.model.MediaConnectedEntity;

import java.util.List;

public interface MediaService {

	MediaResponseDto createMedia(MediaCreateDto request);

	void deleteMedia(int mediaId);

	List<MediaResponseDto> getAllMediaForParent(int parentId, MediaConnectedEntity parentType);

	MediaResponseDto getFirstMediaForParent(int parentId, MediaConnectedEntity parentType);

	MediaResponseDto getMedia(int mediaId);

}
