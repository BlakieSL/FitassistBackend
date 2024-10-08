package source.code.service.declaration;

import source.code.dto.request.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;

import java.util.List;

public interface MediaService {
    MediaResponseDto createMedia(MediaCreateDto request);
    void deleteMedia(int mediaId);
    List<MediaResponseDto> getAllMediaForParent(int parentId, short parentType );
    MediaResponseDto getFirstMediaForParent(int parentId, short parentType);
    MediaResponseDto getMedia(int mediaId);
}
