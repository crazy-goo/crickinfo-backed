package com.debu.crickInfo.dto;

import com.debu.crickInfo.model.BattingStats;
import com.debu.crickInfo.model.BowlingStats;
import com.debu.crickInfo.model.PlayerInfo;
import com.debu.crickInfo.model.Ratings;
import com.debu.crickInfo.model.Video;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerUpsertRequest {

    @Valid
    @NotNull(message = "Player info is required")
    private PlayerInfo player;

    @Valid
    private BattingStats battingStats;

    @Valid
    private BowlingStats bowlingStats;

    private List<String> formats;

    private List<String> searchTags;
    private List<String> strengths;
    private List<String> weaknesses;

    @Valid
    private List<Video> strengthVideos;

    @Valid
    private List<Video> weaknessVideos;

    @Valid
    private Ratings ratings;
}
