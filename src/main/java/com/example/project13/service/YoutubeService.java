package com.example.project13.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class YoutubeService {

    private final OAuth2AuthorizedClientService clientService;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String YOUTUBE_INSERT_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";

    public List<String> extractUrlsFromCsv(MultipartFile file) throws Exception {
        List<String> searchUrls = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length >= 3 && row[2] != null && !row[2].trim().isEmpty()) {
                    searchUrls.add(row[2].trim());
                }
            }
        }

        return searchUrls;
    }

    public File extractVideoIds(List<String> urls) {
        List<String> videoIds = SeleniumRunner.extractVideoIds(urls);

        File csvFile = new File("video_ids_result.csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            writer.writeNext(new String[]{"videoId"});
            for (String id : videoIds) {
                writer.writeNext(new String[]{id});
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return csvFile;
    }

    public void addVideosToPlaylist(List<String> videoIds, String playlistId, Authentication authentication) {
        // 1. 현재 로그인 사용자 인증 정보 기반으로 액세스 토큰 획득
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient("google", authentication.getName());
        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("OAuth2 인증 정보가 없습니다. 먼저 로그인하세요.");
        }
        String accessToken = client.getAccessToken().getTokenValue();

        for (String videoId : videoIds) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = Map.of(
                        "snippet", Map.of(
                                "playlistId", playlistId,
                                "resourceId", Map.of(
                                        "kind", "youtube#video",
                                        "videoId", videoId
                                )
                        )
                );

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(YOUTUBE_INSERT_URL, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("✅ 추가 완료: " + videoId);
                } else {
                    log.warn("⚠️ 실패: {} → {}", videoId, response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("❌ 예외 발생: {} → {}", videoId, e.getMessage());
            }
        }
    }

}
