package com.debu.crickInfo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattingStats {
    private int matches;
    private int innings;
    private int runs;
    private double average;
    private double strikeRate;
    private int highestScore;
    private int centuries;
    private int halfCenturies;
    private int fours;
    private int sixes;
}
