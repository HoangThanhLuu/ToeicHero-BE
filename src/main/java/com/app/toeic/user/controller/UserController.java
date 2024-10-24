package com.app.toeic.user.controller;


import com.app.toeic.external.payload.EmailDTO;
import com.app.toeic.user.enums.EUser;
import com.app.toeic.exception.AppException;
import com.app.toeic.external.response.ResponseVO;
import com.app.toeic.email.service.EmailService;
import com.app.toeic.firebase.service.FirebaseStorageService;
import com.app.toeic.user.payload.*;
import com.app.toeic.user.service.UserService;
import com.app.toeic.util.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final FirebaseStorageService firebaseStorageService;

    private static final String NOT_FOUNT_USER = "Không tìm thấy thông tin người dùng";

    @PostMapping("/register")
    public ResponseVO register(@Valid @RequestBody RegisterDTO registerDto) {
        return userService.register(registerDto);
    }

    @PostMapping("/login-social")
    public ResponseVO loginSocial(@Valid @RequestBody LoginSocialDTO loginSocialDto) {
        var token = userService.loginSocial(loginSocialDto);
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(token)
                .message("LOGIN_SUCCESS")
                .build();
    }

    @PostMapping("/authenticate")
    public ResponseVO authenticate(@Valid @RequestBody LoginDTO loginDto) {
        return userService.authenticate(loginDto);
    }

    @PostMapping("/send-email")
    public ResponseVO sendEmail(@Valid @RequestBody EmailDTO emailDto) {
        emailService.sendEmail(emailDto.getTo(), emailDto.getTemplateCode());
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(null)
                .message("SEND_EMAIL_SUCCESS")
                .build();
    }
    @PostMapping("/send-email-social")
    public Object sendEmailSocial(@Valid @RequestBody LoginSocialDTO emailDto) {
        return userService.loginSocial(emailDto);
    }

    @PostMapping("/forgot-password/{email}")
    public Object forgotPassword(@PathVariable("email") String email){
        return userService.forgotPassword(email);

    }

    @PatchMapping("/update-password")
    public ResponseVO updatePassword(
            @Valid @RequestBody UserUpdatePasswordDTO userUpdatePasswordDto,
            HttpServletRequest request
    ) {
        var profile = userService
                .getProfile(request)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, NOT_FOUNT_USER));
        if (StringUtils.compareIgnoreCase(
                userUpdatePasswordDto.getNewPassword(),
                userUpdatePasswordDto.getConfirmPassword()
        ) != 0) {
            return ResponseVO
                    .builder()
                    .success(Boolean.FALSE)
                    .message("PASSWORD_NOT_MATCH")
                    .build();
        }
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(userService.updatePassword(userUpdatePasswordDto, profile))
                .message("UPDATE_PASSWORD_SUCCESS")
                .build();
    }

    @PatchMapping("/update-profile")
    public ResponseVO updateProfile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            HttpServletRequest request
    ) throws IOException {
        var profile = userService
                .getProfile(request)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, NOT_FOUNT_USER));

        if (StringUtils.isNotBlank(fullName)) {
            profile.setFullName(fullName);
        }
        if (StringUtils.isNotBlank(phone)) {
            profile.setPhone(phone);
        }
        if (StringUtils.isNotBlank(address)) {
            profile.setAddress(address);
        }
        var avatar = firebaseStorageService.uploadFile(file);
        if (StringUtils.isNotBlank(avatar)) {
            profile.setAvatar(avatar);
        }
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(userService.updateProfile(profile))
                .message("UPDATE_PROFILE_SUCCESS")
                .build();
    }

    @PostMapping("/update-avatar")
    public ResponseVO updateAvatar(
            @Valid @RequestBody UserUpdateAvatarDTO updateAvatarDto,
            HttpServletRequest request
    ) {
        var profile = userService
                .getProfile(request)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, NOT_FOUNT_USER));
        profile.setAvatar(updateAvatarDto.getAvatar());
        return userService.updateAvatar(profile);
    }

    @GetMapping("/get-profile")
    public ResponseVO getProfile(HttpServletRequest request) {
        var profile = userService
                .getProfile(request)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, NOT_FOUNT_USER));
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(profile)
                .message("GET_PROFILE_SUCCESS")
                .build();
    }

    @GetMapping("/is-login")
    public ResponseVO isLogin(HttpServletRequest request) {
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(userService.isLogin(request))
                .build();
    }

    @GetMapping("/keep-alive")
    public ResponseVO keepAlive(HttpServletRequest request) {
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(userService.keepAlive(request))
                .build();
    }

    @PostMapping("/confirm-otp")
    public ResponseVO confirmOtp(String email) {
        var user = userService.findByEmail(email);
        user.setStatus(EUser.ACTIVE);
        userService.save(user);
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(null)
                .message("CONFIRM_OTP_SUCCESS")
                .build();
    }

    @GetMapping("/test")
    public ResponseVO test() {
        return new ResponseVO(Boolean.TRUE, "test", "Get assets successfully");
    }
}
