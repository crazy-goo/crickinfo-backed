package com.debu.crickInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseUserSummaryResponse {
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private List<UserSummaryResponse> allUsers;
    private List<UserSummaryResponse> pendingUsers;
}
