package com.debu.crickInfo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
