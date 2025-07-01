package com.example.project13.controller;

import com.example.project13.request.PlaylistRequest;
import com.example.project13.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;

    @PostMapping("/upload")
    public ResponseEntity<Resource> extractVideoIdsFromCsv(@RequestParam("file") MultipartFile file) {
        try {
            List<String> urls = youtubeService.extractUrlsFromCsv(file);

            File csvFile = youtubeService.extractVideoIds(urls);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(csvFile));

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFile.getName()).contentType(MediaType.parseMediaType("text/csv")).body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/playlist/add")
    public ResponseEntity<String> addToPlaylist(@RequestParam String playlistId, @RequestBody PlaylistRequest request, Authentication authentication) {

        System.out.println(">> AUTH: " + authentication);
        System.out.println(">> NAME: " + authentication.getName());
        System.out.println(">> AUTH TYPE: " + authentication.getClass());
        List<String> videoIds = request.getVideoIds();
        youtubeService.addVideosToPlaylist(videoIds, playlistId, authentication);
        return ResponseEntity.ok("추가 완료");
    }
}
