package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.AddMediaDto;
import com.example.simplefullstackproject.dto.MediaDto;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.model.Media;
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

    @GetMapping("/all/{parentId}/{parentType}")
    public ResponseEntity<List<MediaDto>> getAllMediaForParent(
            @PathVariable int parentId,
            @PathVariable short parentType) {
        List<MediaDto> mediaList = mediaService.getAllMediaForParent(parentId, parentType);
        return ResponseEntity.ok(mediaList);
    }

    @GetMapping("/first/{parentId}/{parentType}")
    public ResponseEntity<MediaDto> getFirstMediaForParent(
            @PathVariable int parentId,
            @PathVariable short parentType) {
        MediaDto media = mediaService.getFirstMediaForParent(parentId, parentType);
        return ResponseEntity.ok(media);
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<MediaDto> getMediaById(
            @PathVariable int mediaId) {
        MediaDto media = mediaService.getMediaById(mediaId);
        return ResponseEntity.ok(media);
    }

    @PostMapping
    public ResponseEntity<MediaDto> createMedia(
            @Valid @ModelAttribute AddMediaDto request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        MediaDto response = mediaService.saveMedia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> removeMediaFromParent(
            @PathVariable int mediaId) {
        mediaService.removeMedia(mediaId);
        return ResponseEntity.ok().build();
    }
}