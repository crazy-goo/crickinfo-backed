package com.debu.crickInfo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfo {
    private String name;
    private int age;
    private String country;
    private String team;
    private String role;
    private String battingStyle;
    private String bowlingStyle;
    private int jerseyNumber;
    private String imageUrl;
}
