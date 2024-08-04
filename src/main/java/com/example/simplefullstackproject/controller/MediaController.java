package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.AddMediaDto;
import com.example.simplefullstackproject.dto.MediaDto;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<MediaDto>> getAllMediaForParent(@PathVariable Integer parentId) {
        List<MediaDto> mediaList = mediaService.findAllMediaForParent(parentId);
        return ResponseEntity.ok(mediaList);
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<MediaDto> getMediaById(@PathVariable Integer mediaId) {
        MediaDto media = mediaService.getMediaById(mediaId);
        return ResponseEntity.ok(media);
    }

    @PostMapping
    public ResponseEntity<MediaDto> createMedia(
            @Valid @RequestBody AddMediaDto request,
            BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        MediaDto response = mediaService.saveMedia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{mediaId}/parent/{parentId}")
    public ResponseEntity<Void> removeMediaFromParent(
            @PathVariable Integer mediaId, @PathVariable Integer parentId) {
        mediaService.removeMediaFromParent(mediaId, parentId);
        return ResponseEntity.ok().build();
    }
}