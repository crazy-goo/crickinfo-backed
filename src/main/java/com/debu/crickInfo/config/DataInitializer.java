package com.debu.crickInfo.config;

import com.debu.crickInfo.model.BattingStats;
import com.debu.crickInfo.model.BowlingStats;
import com.debu.crickInfo.model.Meta;
import com.debu.crickInfo.model.Player;
import com.debu.crickInfo.model.PlayerInfo;
import com.debu.crickInfo.model.Ratings;
import com.debu.crickInfo.model.Video;
import com.debu.crickInfo.repository.PlayerRepository;
import com.debu.crickInfo.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PlayerRepository playerRepository;
    private final AuthService authService;

    public DataInitializer(PlayerRepository playerRepository, AuthService authService) {
        this.playerRepository = playerRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        authService.initializeDefaultAdmin();

        if (playerRepository.count() == 0) {
            seedPlayers();
        }
    }

    private void seedPlayers() {
        Player virat = Player.builder()
                .player(new PlayerInfo("Virat Kohli", 37, "India", "RCB", "Top Batter", "Right-hand Bat", "Right-arm Medium", 18, "https://example.com/virat.jpg"))
                .battingStats(new BattingStats(550, 530, 26000, 53.4, 92.1, 183, 80, 135, 2500, 320))
                .bowlingStats(new BowlingStats(110.0, 8, 6.1, 45.0, "1/15", 0))
                .formats(List.of("ODI", "T20", "TEST"))
                .searchTags(List.of("top batter", "chase master", "cover drive"))
                .strengths(List.of("Chasing under pressure", "Cover drive", "Strike rotation"))
                .weaknesses(List.of("Early outswing movement", "Leg-side trap against short ball"))
                .strengthVideos(List.of(
                        new Video("Chasing masterclass", "Strength", "How he controls the chase", "Australia", "ODI", "https://example.com/videos/virat-strength.mp4", "https://example.com/thumbs/virat-strength.jpg")
                ))
                .weaknessVideos(List.of(
                        new Video("Outswing dismissal patterns", "Weakness", "Edges against new-ball channel", "England", "TEST", "https://example.com/videos/virat-weakness.mp4", "https://example.com/thumbs/virat-weakness.jpg")
                ))
                .ratings(new Ratings(95, 97, 91))
                .meta(new Meta("system", LocalDateTime.now(), LocalDateTime.now()))
                .build();

        Player rashid = Player.builder()
                .player(new PlayerInfo("Rashid Khan", 27, "Afghanistan", "GT", "Bowler", "Right-hand Bat", "Legbreak Googly", 19, "https://example.com/rashid.jpg"))
                .battingStats(new BattingStats(260, 140, 1900, 18.4, 148.0, 79, 0, 5, 120, 110))
                .bowlingStats(new BowlingStats(920.0, 390, 6.3, 18.5, "5/3", 9))
                .formats(List.of("T20", "ODI"))
                .searchTags(List.of("top bowler", "leg spinner", "matchup bowler"))
                .strengths(List.of("Middle-over wicket taking", "Googly variation", "Containment in T20"))
                .weaknesses(List.of("Can be lined up by left-hand finishers on smaller grounds"))
                .strengthVideos(List.of(
                        new Video("Googly setup", "Strength", "How he sets batters up over two overs", "Pakistan", "T20", "https://example.com/videos/rashid-strength.mp4", "https://example.com/thumbs/rashid-strength.jpg")
                ))
                .weaknessVideos(List.of(
                        new Video("Left-hander attack map", "Weakness", "Boundary options lefties use against him", "England", "T20", "https://example.com/videos/rashid-weakness.mp4", "https://example.com/thumbs/rashid-weakness.jpg")
                ))
                .ratings(new Ratings(94, 92, 88))
                .meta(new Meta("system", LocalDateTime.now(), LocalDateTime.now()))
                .build();

        playerRepository.saveAll(List.of(virat, rashid));
    }
}
