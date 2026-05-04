package com.debu.crickInfo.controller;

import com.debu.crickInfo.dto.AdminCredentialUpdateRequest;
import com.debu.crickInfo.dto.ApprovalUpdateRequest;
import com.debu.crickInfo.dto.FranchiseUserSummaryResponse;
import com.debu.crickInfo.dto.LoginResponse;
import com.debu.crickInfo.dto.PlayerUpsertRequest;
import com.debu.crickInfo.dto.UserSummaryResponse;
import com.debu.crickInfo.model.Player;
import com.debu.crickInfo.model.Video;
import com.debu.crickInfo.service.AuthService;
import com.debu.crickInfo.service.FileStorageService;
import com.debu.crickInfo.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    private final PlayerService playerService;
    private final AuthService authService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    public AdminController(
            PlayerService playerService,
            AuthService authService,
            FileStorageService fileStorageService,
            ObjectMapper objectMapper
    ) {
        this.playerService = playerService;
        this.authService = authService;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    @PutMapping("/credentials")
    public ResponseEntity<LoginResponse> updateCredentials(
            @Valid @RequestBody AdminCredentialUpdateRequest request,
            Authentication authentication
    ) {
        LoginResponse response = authService.updateAdminCredentials(authentication.getName(), request);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/franchise-users")
    public ResponseEntity<List<UserSummaryResponse>> getAllFranchiseUsers() {
        return ResponseEntity.ok(authService.getAllFranchiseUsers());
    }

    @GetMapping("/franchise-users/summary")
    public ResponseEntity<FranchiseUserSummaryResponse> getFranchiseUserSummary() {
        return ResponseEntity.ok(authService.getFranchiseUserSummary());
    }

    @GetMapping("/franchise-users/pending")
    public ResponseEntity<List<UserSummaryResponse>> getPendingFranchiseUsers() {
        return ResponseEntity.ok(authService.getPendingFranchiseUsers());
    }

    @GetMapping("/franchise-users/status/{status}")
    public ResponseEntity<List<UserSummaryResponse>> getFranchiseUsersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(authService.getFranchiseUsersByStatus(status));
    }

    @GetMapping("/franchise-users/{id}")
    public ResponseEntity<UserSummaryResponse> getFranchiseUserById(@PathVariable String id) {
        return ResponseEntity.ok(authService.getFranchiseUserById(id));
    }

    @PatchMapping("/franchise-users/{id}/approval")
    public ResponseEntity<UserSummaryResponse> updateFranchiseApproval(
            @PathVariable String id,
            @Valid @RequestBody ApprovalUpdateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(authService.updateFranchiseApproval(id, authentication.getName(), request));
    }

    @PatchMapping("/franchise-users/{id}/approve")
    public ResponseEntity<UserSummaryResponse> approveFranchiseUser(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                authService.updateFranchiseApproval(
                        id,
                        authentication.getName(),
                        ApprovalUpdateRequest.builder().status("APPROVED").build()
                )
        );
    }

    @PatchMapping("/franchise-users/{id}/reject")
    public ResponseEntity<UserSummaryResponse> rejectFranchiseUser(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                authService.updateFranchiseApproval(
                        id,
                        authentication.getName(),
                        ApprovalUpdateRequest.builder().status("REJECTED").build()
                )
        );
    }

    @PostMapping(value = "/players", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(
            @Valid @RequestBody PlayerUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerService.createPlayer(request, authentication.getName()));
    }

    @PostMapping(value = "/players", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Player> createPlayerWithFiles(
            @RequestParam("playerData") String playerData,
            @RequestParam(value = "playerImage", required = false) MultipartFile playerImage,
            @RequestParam(value = "strengthVideos", required = false) List<MultipartFile> strengthVideos,
            @RequestParam(value = "weaknessVideos", required = false) List<MultipartFile> weaknessVideos,
            @RequestParam(value = "strengthThumbnails", required = false) List<MultipartFile> strengthThumbnails,
            @RequestParam(value = "weaknessThumbnails", required = false) List<MultipartFile> weaknessThumbnails,
            Authentication authentication
    ) throws IOException {
        PlayerUpsertRequest request = objectMapper.readValue(playerData, PlayerUpsertRequest.class);
        attachPlayerImage(request, playerImage);
        request.setStrengthVideos(
                attachUploadedVideos(request.getStrengthVideos(), strengthVideos, strengthThumbnails, "players/strength")
        );
        request.setWeaknessVideos(
                attachUploadedVideos(request.getWeaknessVideos(), weaknessVideos, weaknessThumbnails, "players/weakness")
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerService.createPlayer(request, authentication.getName()));
    }

    @PutMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable String id,
            @Valid @RequestBody PlayerUpsertRequest request,
            Authentication authentication
    ) {
        Player updatedPlayer = playerService.updatePlayer(id, request, authentication.getName());
        if (updatedPlayer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPlayer);
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String id) {
        if (playerService.deletePlayer(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private void attachPlayerImage(PlayerUpsertRequest request, MultipartFile playerImage) throws IOException {
        if (playerImage == null || playerImage.isEmpty()) {
            return;
        }

        if (request.getPlayer() == null) {
            throw new IllegalArgumentException("Player info is required");
        }

        request.getPlayer().setImageUrl(fileStorageService.store(playerImage, "players/images"));
    }

    private List<Video> attachUploadedVideos(
            List<Video> videoMetadata,
            List<MultipartFile> videoFiles,
            List<MultipartFile> thumbnailFiles,
            String subDirectory
    ) throws IOException {
        List<MultipartFile> safeVideoFiles = videoFiles == null ? List.of() : videoFiles;
        List<MultipartFile> safeThumbnailFiles = thumbnailFiles == null ? List.of() : thumbnailFiles;

        if (safeVideoFiles.isEmpty()) {
            return videoMetadata == null ? new ArrayList<>() : videoMetadata;
        }

        if (!safeThumbnailFiles.isEmpty() && safeThumbnailFiles.size() != safeVideoFiles.size()) {
            throw new IllegalArgumentException("Video files and thumbnail files count must match");
        }

        List<Video> safeMetadata = videoMetadata == null ? new ArrayList<>() : videoMetadata;
        if (!safeMetadata.isEmpty() && safeMetadata.size() != safeVideoFiles.size()) {
            throw new IllegalArgumentException("Video metadata count must match uploaded video files count");
        }

        while (safeMetadata.size() < safeVideoFiles.size()) {
            safeMetadata.add(new Video());
        }

        for (int index = 0; index < safeVideoFiles.size(); index++) {
            Video video = safeMetadata.get(index);
            video.setVideoUrl(fileStorageService.store(safeVideoFiles.get(index), subDirectory + "/videos"));

            if (!safeThumbnailFiles.isEmpty()) {
                video.setThumbnailUrl(fileStorageService.store(safeThumbnailFiles.get(index), subDirectory + "/thumbnails"));
            }
        }

        return safeMetadata;
    }
}
