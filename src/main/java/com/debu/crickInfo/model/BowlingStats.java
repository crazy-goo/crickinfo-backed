package com.debu.crickInfo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BowlingStats {
    private double overs;
    private int wickets;
    private double economy;
    private double bowlingAverage;
    private String bestFigures;
    private int maidens;
}
