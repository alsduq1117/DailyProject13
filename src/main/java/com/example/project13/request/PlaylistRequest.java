package com.example.project13.request;

import java.util.List;

public class PlaylistRequest {
    private List<String> videoIds;

    public List<String> getVideoIds() {
        return videoIds;
    }

    public void setVideoIds(List<String> videoIds) {
        this.videoIds = videoIds;
    }
}