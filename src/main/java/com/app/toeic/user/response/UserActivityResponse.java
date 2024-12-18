package com.app.toeic.user.response;

import java.time.LocalDateTime;

public interface UserActivityResponse {
    Long getUserAccountLogId();

    String getOldData();

    String getNewData();

    String getAction();

    String getLastIpAddress();

    LocalDateTime getCreatedAt();

    interface UserActivity2Response extends UserActivityResponse {
        UserAccountInfoResponse getUserAccount();
    }

    interface UserAccountInfoResponse {
        String getEmail();
        String getFullName();
    }
}
