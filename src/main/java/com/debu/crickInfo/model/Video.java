package com.debu.crickInfo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    private String title;
    private String type;
    private String description;
    private String opponent;
    private String matchFormat;
    private String videoUrl;
    private String thumbnailUrl;
}
