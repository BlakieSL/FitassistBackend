package source.code.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.service.declaration.MediaService;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {
  private final MediaService mediaService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @GetMapping("/all/{parentId}/{parentType}")
  public ResponseEntity<List<MediaResponseDto>> getAllMediaForParent(@PathVariable int parentId,
                                                                     @PathVariable short parentType) {

    List<MediaResponseDto> mediaList = mediaService.getAllMediaForParent(parentId, parentType);
    return ResponseEntity.ok(mediaList);
  }

  @GetMapping("/first/{parentId}/{parentType}")
  public ResponseEntity<MediaResponseDto> getFirstMediaForParent(@PathVariable int parentId,
                                                                 @PathVariable short parentType) {
    MediaResponseDto media = mediaService.getFirstMediaForParent(parentId, parentType);
    return ResponseEntity.ok(media);
  }

  @GetMapping("/{mediaId}")
  public ResponseEntity<MediaResponseDto> getMedia(@PathVariable int mediaId) {
    MediaResponseDto media = mediaService.getMedia(mediaId);
    return ResponseEntity.ok(media);
  }

  @PostMapping
  public ResponseEntity<MediaResponseDto> createMedia(
          @Valid @ModelAttribute MediaCreateDto request) {
    MediaResponseDto response = mediaService.createMedia(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{mediaId}")
  public ResponseEntity<Void> removeMediaFromParent(@PathVariable int mediaId) {
    mediaService.deleteMedia(mediaId);
    return ResponseEntity.ok().build();
  }
}