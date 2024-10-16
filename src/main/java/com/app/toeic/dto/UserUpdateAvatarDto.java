package com.app.toeic.dto;


import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserUpdateAvatarDto implements Serializable {
    @Email(message = "Email không hợp lệ")
    String email;
    String avatar;
}