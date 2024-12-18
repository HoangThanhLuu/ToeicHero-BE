package com.app.toeic.user.service;

import com.app.toeic.user.enums.UType;
import com.app.toeic.user.model.UserAccount;
import com.app.toeic.external.response.ResponseVO;
import com.app.toeic.user.payload.*;
import com.app.toeic.user.response.UserAccountRepsonse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface UserService {
    ResponseVO authenticate(LoginDTO loginDto);

    ResponseVO register(RegisterDTO registerDto);

    ResponseVO getAllUser();

    ResponseVO updateUser(UserDTO user);

    Optional<UserAccount> getCurrentUser();


    UserAccount findByEmail(String email);

    ResponseVO updatePassword(String email, String newPassword);

    Object updatePassword(UserUpdatePasswordDTO userUpdateDto, UserAccount user);

    void updateAvatar(UserAccount userAccount);

    Optional<UserAccount> getProfile(HttpServletRequest request);

    Boolean isLogin(HttpServletRequest request);

    void save(UserAccount userAccount);

    Boolean keepAlive(HttpServletRequest request);

    void updateProfile(UserAccount profile);

    Object loginSocial(LoginSocialDTO loginSocialDto);

    Object isAdminLogin(HttpServletRequest request);

    String forgotPassword(String email);

    Object getActivities(HttpServletRequest request, int page, int size, String type);

    Object getActivities(int page, int size, String type, String fromDate, String toDate);

    LoginDTO readCaptcha(HttpServletRequest request);

    boolean checkMultipleLogin(HttpServletRequest request);

    boolean isValidCaptcha(HttpServletRequest request, String captcha);

    Optional<UserAccountRepsonse> getProfileV2(HttpServletRequest request);

    Object ultimateLogin(String username);

    Object updateUserType(HttpServletRequest type);

    String randomPassword();
}
