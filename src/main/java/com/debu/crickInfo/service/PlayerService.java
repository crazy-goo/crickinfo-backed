package com.debu.crickInfo.service;

import com.debu.crickInfo.dto.PlayerUpsertRequest;
import com.debu.crickInfo.model.BattingStats;
import com.debu.crickInfo.model.BowlingStats;
import com.debu.crickInfo.model.Meta;
import com.debu.crickInfo.model.Player;
import com.debu.crickInfo.model.PlayerInfo;
import com.debu.crickInfo.model.Ratings;
import com.debu.crickInfo.model.Video;
import com.debu.crickInfo.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparing(this::getPlayerName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<Player> getPlayerById(String id) {
        return playerRepository.findById(id);
    }

    public Player createPlayer(PlayerUpsertRequest request, String createdBy) {
        Player player = mapRequestToPlayer(request);

        Meta meta = new Meta();
        meta.setCreatedBy(createdBy);
        meta.setCreatedAt(LocalDateTime.now());
        meta.setUpdatedAt(LocalDateTime.now());
        player.setMeta(meta);

        return playerRepository.save(player);
    }

    public Player updatePlayer(String id, PlayerUpsertRequest request, String updatedBy) {
        return playerRepository.findById(id)
                .map(existingPlayer -> {
                    Player updatedPlayer = mapRequestToPlayer(request);
                    existingPlayer.setPlayer(updatedPlayer.getPlayer());
                    existingPlayer.setBattingStats(updatedPlayer.getBattingStats());
                    existingPlayer.setBowlingStats(updatedPlayer.getBowlingStats());
                    existingPlayer.setFormats(updatedPlayer.getFormats());
                    existingPlayer.setSearchTags(updatedPlayer.getSearchTags());
                    existingPlayer.setStrengths(updatedPlayer.getStrengths());
                    existingPlayer.setWeaknesses(updatedPlayer.getWeaknesses());
                    existingPlayer.setStrengthVideos(updatedPlayer.getStrengthVideos());
                    existingPlayer.setWeaknessVideos(updatedPlayer.getWeaknessVideos());
                    existingPlayer.setRatings(updatedPlayer.getRatings());

                    if (existingPlayer.getMeta() == null) {
                        existingPlayer.setMeta(new Meta());
                        existingPlayer.getMeta().setCreatedBy(updatedBy);
                        existingPlayer.getMeta().setCreatedAt(LocalDateTime.now());
                    }
                    existingPlayer.getMeta().setCreatedBy(
                            existingPlayer.getMeta().getCreatedBy() == null ? updatedBy : existingPlayer.getMeta().getCreatedBy()
                    );
                    existingPlayer.getMeta().setUpdatedAt(LocalDateTime.now());

                    return playerRepository.save(existingPlayer);
                })
                .orElse(null);
    }

    public boolean deletePlayer(String id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Player> searchPlayers(String keyword, String role, String format, String country, String team) {
        return playerRepository.findAll().stream()
                .filter(player -> matchesKeyword(player, keyword))
                .filter(player -> matchesField(player.getPlayer() != null ? player.getPlayer().getRole() : null, role))
                .filter(player -> matchesList(player.getFormats(), format))
                .filter(player -> matchesField(player.getPlayer() != null ? player.getPlayer().getCountry() : null, country))
                .filter(player -> matchesField(player.getPlayer() != null ? player.getPlayer().getTeam() : null, team))
                .sorted(Comparator.comparing(this::getPlayerName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private Player mapRequestToPlayer(PlayerUpsertRequest request) {
        Player player = new Player();
        player.setPlayer(defaultPlayerInfo(request.getPlayer()));
        player.setBattingStats(defaultBattingStats(request.getBattingStats()));
        player.setBowlingStats(defaultBowlingStats(request.getBowlingStats()));
        player.setFormats(defaultFormats(request.getFormats()));
        player.setStrengths(normalizeList(request.getStrengths()));
        player.setWeaknesses(normalizeList(request.getWeaknesses()));
        player.setStrengthVideos(normalizeVideos(request.getStrengthVideos()));
        player.setWeaknessVideos(normalizeVideos(request.getWeaknessVideos()));
        player.setSearchTags(buildSearchTags(player, request.getSearchTags()));
        player.setRatings(defaultRatings(request.getRatings()));
        return player;
    }

    private boolean matchesKeyword(Player player, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        List<String> searchableValues = new ArrayList<>();

        if (player.getPlayer() != null) {
            searchableValues.add(player.getPlayer().getName());
            searchableValues.add(player.getPlayer().getCountry());
            searchableValues.add(player.getPlayer().getTeam());
            searchableValues.add(player.getPlayer().getRole());
            searchableValues.add(player.getPlayer().getBattingStyle());
            searchableValues.add(player.getPlayer().getBowlingStyle());
        }

        searchableValues.addAll(nullSafeList(player.getFormats()));
        searchableValues.addAll(nullSafeList(player.getSearchTags()));
        searchableValues.addAll(nullSafeList(player.getStrengths()));
        searchableValues.addAll(nullSafeList(player.getWeaknesses()));
        collectVideoSearchableValues(searchableValues, player.getStrengthVideos());
        collectVideoSearchableValues(searchableValues, player.getWeaknessVideos());

        return searchableValues.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(normalizedKeyword));
    }

    private void collectVideoSearchableValues(List<String> searchableValues, List<Video> videos) {
        for (Video video : nullSafeList(videos)) {
            searchableValues.add(video.getTitle());
            searchableValues.add(video.getType());
            searchableValues.add(video.getDescription());
            searchableValues.add(video.getOpponent());
            searchableValues.add(video.getMatchFormat());
        }
    }

    private boolean matchesField(String actualValue, String requestedValue) {
        if (requestedValue == null || requestedValue.isBlank()) {
            return true;
        }
        return actualValue != null && actualValue.equalsIgnoreCase(requestedValue.trim());
    }

    private boolean matchesList(List<String> values, String requestedValue) {
        if (requestedValue == null || requestedValue.isBlank()) {
            return true;
        }
        return nullSafeList(values).stream().anyMatch(value -> value.equalsIgnoreCase(requestedValue.trim()));
    }

    private List<String> normalizeList(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }

        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }

    private PlayerInfo defaultPlayerInfo(PlayerInfo playerInfo) {
        if (playerInfo == null) {
            throw new IllegalArgumentException("Player info is required");
        }
        return playerInfo;
    }

    private BattingStats defaultBattingStats(BattingStats battingStats) {
        return battingStats == null ? new BattingStats(0, 0, 0, 0.0, 0.0, 0, 0, 0, 0, 0) : battingStats;
    }

    private BowlingStats defaultBowlingStats(BowlingStats bowlingStats) {
        return bowlingStats == null ? new BowlingStats(0.0, 0, 0.0, 0.0, "", 0) : bowlingStats;
    }

    private Ratings defaultRatings(Ratings ratings) {
        return ratings == null ? new Ratings(0, 0, 0) : ratings;
    }

    private List<String> defaultFormats(List<String> formats) {
        List<String> normalizedFormats = normalizeList(formats);
        return normalizedFormats.isEmpty() ? List.of("GENERAL") : normalizedFormats;
    }

    private List<String> buildSearchTags(Player player, List<String> incomingTags) {
        LinkedHashSet<String> tags = new LinkedHashSet<>(normalizeList(incomingTags));

        if (player.getPlayer() != null) {
            addTag(tags, player.getPlayer().getName());
            addTag(tags, player.getPlayer().getRole());
            addTag(tags, player.getPlayer().getTeam());
            addTag(tags, player.getPlayer().getCountry());
        }

        player.getFormats().forEach(format -> addTag(tags, format));
        player.getStrengths().forEach(strength -> addTag(tags, strength));
        player.getWeaknesses().forEach(weakness -> addTag(tags, weakness));

        return new ArrayList<>(tags);
    }

    private void addTag(LinkedHashSet<String> tags, String value) {
        if (value != null && !value.isBlank()) {
            tags.add(value.trim());
        }
    }

    private List<Video> normalizeVideos(List<Video> videos) {
        if (videos == null) {
            return new ArrayList<>();
        }

        return videos.stream()
                .filter(video -> video != null && video.getVideoUrl() != null && !video.getVideoUrl().isBlank())
                .toList();
    }

    private <T> List<T> nullSafeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private String getPlayerName(Player player) {
        if (player.getPlayer() == null || player.getPlayer().getName() == null) {
            return "";
        }
        return player.getPlayer().getName();
    }
}
