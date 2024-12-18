package com.app.toeic.user.payload;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserUpdateDTO {
    @NotEmpty(message = "Tên không được bỏ trống")
    String fullName;

    @NotEmpty(message = "Số điện thoại không được bỏ trống")
    String phone;

    @NotEmpty(message = "Địa chỉ không được bỏ trống")
    String address;

    String avatar;
}
