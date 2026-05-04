package com.debu.crickInfo.controller;

import com.debu.crickInfo.model.Player;
import com.debu.crickInfo.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin("*")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getPlayers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String team
    ) {
        if (keyword == null && role == null && format == null && country == null && team == null) {
            return ResponseEntity.ok(playerService.getAllPlayers());
        }
        return ResponseEntity.ok(playerService.searchPlayers(keyword, role, format, country, team));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Player>> searchPlayers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String team
    ) {
        return ResponseEntity.ok(playerService.searchPlayers(keyword, role, format, country, team));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
