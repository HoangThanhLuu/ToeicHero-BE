package com.app.toeic.user.payload;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserUpdateDto implements Serializable {
    @NotEmpty(message = "Tên không được bỏ trống")
    String fullName;

    @NotEmpty(message = "Số điện thoại không được bỏ trống")
    String phone;

    @NotEmpty(message = "Địa chỉ không được bỏ trống")
    String address;

    String avatar;
}