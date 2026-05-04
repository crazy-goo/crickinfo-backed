package com.debu.crickInfo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "players")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {
    @Id
    private String id;
    private PlayerInfo player;
    private BattingStats battingStats;
    private BowlingStats bowlingStats;
    private List<String> formats;
    private List<String> searchTags;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<Video> strengthVideos;
    private List<Video> weaknessVideos;
    private Ratings ratings;
    private Meta meta;
}
