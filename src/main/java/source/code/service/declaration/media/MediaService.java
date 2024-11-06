package source.code.service.declaration.media;

import source.code.dto.Request.media.MediaCreateDto;
import source.code.dto.Response.MediaResponseDto;

import java.util.List;

public interface MediaService {
    MediaResponseDto createMedia(MediaCreateDto request);

    void deleteMedia(int mediaId);

    List<MediaResponseDto> getAllMediaForParent(int parentId, short parentType);

    MediaResponseDto getFirstMediaForParent(int parentId, short parentType);

    MediaResponseDto getMedia(int mediaId);
}
