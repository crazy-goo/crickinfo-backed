package com.debu.crickInfo.dto;

import com.debu.crickInfo.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerResponse {
    private String message;
    private boolean success;
    private Player player;
}
