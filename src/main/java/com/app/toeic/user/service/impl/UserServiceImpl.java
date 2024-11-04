package com.app.toeic.user.service.impl;

import com.app.toeic.email.service.impl.EmailServiceImpl;
import com.app.toeic.user.enums.ERole;
import com.app.toeic.user.enums.EUser;
import com.app.toeic.exception.AppException;
import com.app.toeic.jwt.JwtTokenProvider;
import com.app.toeic.user.model.UserAccount;
import com.app.toeic.user.payload.*;
import com.app.toeic.user.repo.IRoleRepository;
import com.app.toeic.user.repo.IUserAccountLogRepository;
import com.app.toeic.user.repo.IUserAccountRepository;
import com.app.toeic.user.response.LoginResponse;
import com.app.toeic.external.response.ResponseVO;
import com.app.toeic.user.service.UserService;
import com.app.toeic.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

@Log
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    AuthenticationManager authenticationManager;
    IUserAccountRepository iUserRepository;
    IRoleRepository iRoleRepository;
    PasswordEncoder passwordEncoder;
    JwtTokenProvider jwtUtilities;
    UserDetailsService userDetailsService;
    EmailServiceImpl emailService;
    IUserAccountLogRepository iUserAccountLogRepository;

    private static final String EMAIL_NOT_REGISTERED = "EMAIL_NOT_REGISTERED";

    @Override
    public ResponseVO authenticate(LoginDTO loginDto) {
        var v1 = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        var authentication = authenticationManager.authenticate(v1);
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
        var user = iUserRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new AppException(HttpStatus.SEE_OTHER, EMAIL_NOT_REGISTERED));
        if (EUser.INACTIVE.equals(user.getStatus())) {
            return ResponseVO
                    .builder()
                    .success(Boolean.FALSE)
                    .data(user.getStatus())
                    .message("ACCOUNT_NOT_ACTIVE")
                    .build();
        } else if (EUser.BLOCKED.equals(user.getStatus())) {
            return ResponseVO
                    .builder()
                    .success(Boolean.FALSE)
                    .message("ACCOUNT_BLOCKED")
                    .build();
        }

        var rolesNames = new ArrayList<String>();
        user.getRoles().forEach(r -> rolesNames.add(r.getRoleName()));
        var token = jwtUtilities.generateToken(user.getUsername(), user.getPassword(), rolesNames);
        var res = LoginResponse
                .builder()
                .token(token)
                .email(user.getEmail())
                .roles(rolesNames)
                .build();
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(res)
                .message("LOGIN_SUCCESS")
                .build();
    }

    @Override
    public ResponseVO register(RegisterDTO registerDto) {
        if (Boolean.TRUE.equals(iUserRepository.existsByEmail(registerDto.getEmail()))) {
            throw new AppException(HttpStatus.SEE_OTHER, "EMAIL_EXISTED");
        }
        emailService.sendEmail(registerDto.getEmail(), "AUTHENTICATION_AFTER_REGISTER");
        var user = UserAccount
                .builder()
                .email(registerDto.getEmail())
                .fullName(registerDto.getFullName())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .avatar(AvatarHelper.getAvatar(""))
                .roles(Collections.singleton(iRoleRepository.findByRoleName(ERole.USER)))
                .status(EUser.INACTIVE)
                .provider("TOEICUTE")
                .build();
        iUserRepository.save(user);
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(null)
                .message("REGISTER_SUCCESS")
                .build();
    }

    @Override
    public ResponseVO getAllUser() {
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(iUserRepository.findAllByRolesNotContains(iRoleRepository.findByRoleName(ERole.ADMIN)))
                .message("GET_ALL_USER_SUCCESS")
                .build();
    }

    @Override
    public ResponseVO updateUser(UserDTO user) {
        var u = iUserRepository
                .findById(user.getUserId())
                .orElseThrow(() -> new AppException(HttpStatus.SEE_OTHER, "USER_NOT_FOUND"));
        u.setStatus(user.getStatus());
        iUserRepository.save(u);
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(iUserRepository.save(u))
                .message("UPDATE_USER_SUCCESS")
                .build();
    }

    @Override
    public Optional<UserAccount> getCurrentUser() {
        var requestContext = RequestContextHolder.getRequestAttributes();
        if (requestContext == null) {
            return Optional.empty();
        }
        var request = ((ServletRequestAttributes) requestContext).getRequest();
        return getProfile(request);
    }

    @Override
    public UserAccount findByEmail(String email) {
        return iUserRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.SEE_OTHER, EMAIL_NOT_REGISTERED));
    }

    @Override
    public ResponseVO updatePassword(String email, String newPassword) {
        var user = iUserRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.SEE_OTHER, EMAIL_NOT_REGISTERED));
        user.setPassword(passwordEncoder.encode(newPassword));
        iUserRepository.save(user);
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(null)
                .message("UPDATE_PASSWORD_SUCCESS")
                .build();
    }

    @Override
    public Object updatePassword(UserUpdatePasswordDTO userUpdateDto, UserAccount user) {
        if (!passwordEncoder.matches(userUpdateDto.getCurrentPassword(), user.getPassword())) {
            throw new AppException(HttpStatus.SEE_OTHER, "CURRENT_PASSWORD_NOT_MATCH");
        }
        user.setPassword(passwordEncoder.encode(userUpdateDto.getNewPassword()));
        iUserRepository.save(user);
        return "Cập nhật thông tin thành công!";
    }

    @Override
    public ResponseVO updateAvatar(UserAccount userAccount) {
        iUserRepository.save(userAccount);
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(userAccount.getAvatar())
                .message("UPDATE_AVATAR_SUCCESS")
                .build();
    }

    @Override
    public Optional<UserAccount> getProfile(HttpServletRequest request) {
        var token = jwtUtilities.getToken(request);
        if (StringUtils.isNotEmpty(token) && jwtUtilities.validateToken(token)) {
            String email = jwtUtilities.extractUsername(token);

            var userDetails = userDetailsService.loadUserByUsername(email);
            if (Boolean.TRUE.equals(jwtUtilities.validateToken(token, userDetails))) {
                var user = iUserRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new AppException(HttpStatus.SEE_OTHER, EMAIL_NOT_REGISTERED));
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Boolean isLogin(HttpServletRequest request) {
        var token = jwtUtilities.getToken(request);
        if (StringUtils.isNotEmpty(token) && jwtUtilities.validateToken(token)) {
            String email = jwtUtilities.extractUsername(token);

            var userDetails = userDetailsService.loadUserByUsername(email);
            return jwtUtilities.validateToken(token, userDetails);
        }
        return Boolean.FALSE;
    }

    @Override
    public void save(UserAccount userAccount) {
        iUserRepository.save(userAccount);
    }

    @Override
    public Boolean keepAlive(HttpServletRequest request) {
        var token = jwtUtilities.getToken(request);
        if (StringUtils.isNotEmpty(token) && jwtUtilities.validateToken(token)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Object updateProfile(UserAccount profile) {
        return iUserRepository.save(profile);
    }

    @Override
    public Object loginSocial(LoginSocialDTO loginSocialDto) {
        var user = iUserRepository.findByEmail(loginSocialDto.getEmail());
        var tokens = new ArrayList<String>();
        user.ifPresentOrElse(u -> {
            if (u.getStatus()
                 .equals(EUser.INACTIVE)) {
                throw new AppException(HttpStatus.SEE_OTHER, "ACCOUNT_NOT_ACTIVE");
            } else if (u.getStatus()
                        .equals(EUser.BLOCKED)) {
                throw new AppException(HttpStatus.SEE_OTHER, "ACCOUNT_BLOCKED");
            } else if (!u.getProvider()
                         .equals(loginSocialDto.getProvider())) {
                throw new AppException(
                        HttpStatus.SEE_OTHER,
                        "EMAIL_EXISTED_WITH_OTHER_PROVIDER"
                );
            }
            var rolesNames = new ArrayList<String>();
            u.getRoles().forEach(r -> rolesNames.add(r.getRoleName()));
            final var token = jwtUtilities.generateToken(u.getUsername(), u.getPassword(), rolesNames);
            tokens.add(token);
        }, () -> {
            var password = randomPassword();
            var newUser = UserAccount
                    .builder()
                    .email(loginSocialDto.getEmail())
                    .fullName(loginSocialDto.getFullName())
                    .avatar(loginSocialDto.getAvatar())
                    .roles(Collections.singleton(iRoleRepository.findByRoleName(ERole.USER)))
                    .status(EUser.ACTIVE)
                    .provider(loginSocialDto.getProvider())
                    .password(passwordEncoder.encode(password))
                    .build();
            iUserRepository.save(newUser);
            List<String> rolesNames = new ArrayList<>();
            newUser.getRoles()
                   .forEach(r -> rolesNames.add(r.getRoleName()));
            final var token = jwtUtilities.generateToken(newUser.getUsername(), newUser.getPassword(), rolesNames);
            tokens.add(token);
            emailService.sendEmailAccount(loginSocialDto, password, "LOGIN_SOCIAL");
        });
        return tokens.getFirst();
    }

    @Override
    public Object isAdminLogin(HttpServletRequest request) {
        var token = jwtUtilities.getToken(request);
        if (StringUtils.isNotEmpty(token) && jwtUtilities.validateToken(token)) {
            String email = jwtUtilities.extractUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            var listRoles = jwtUtilities.extractRoles(token);

            if (Boolean.TRUE.equals(
                    jwtUtilities.validateToken(token, userDetails))
                    && listRoles
                    .stream()
                    .map(String::valueOf)
                    .anyMatch(r -> r.contains(ERole.ADMIN.name()))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public String forgotPassword(String email) {
        emailService.sendEmail(email, "FORGOT_PASSWORD");
        return "SEND_EMAIL_SUCCESS";
    }

    @Override
    public Object getActivities(HttpServletRequest request, int page, int pageSize, String type) {
        var profile = getProfile(request);
        if (profile.isPresent()) {
            return getListActivity(page, pageSize, type, profile.get(), null, null);
        }
        return Collections.emptyList();
    }

    private Object getListActivity(int page, int pageSize, String type, UserAccount account, LocalDateTime dateFrom, LocalDateTime dateTo) {
        if ("ALL".equals(type)) {
            return account != null
                    ? iUserAccountLogRepository.findAllByUserAccount(account, PageRequest.of(page, pageSize, Sort.by("createdAt").descending()))
                    : iUserAccountLogRepository.findAllByCreatedAtBetween(dateFrom, dateTo ,PageRequest.of(page, pageSize, Sort.by("createdAt").descending()));
        }
        return account != null
                ? iUserAccountLogRepository.findAllByUserAccountAndAction(account, type, PageRequest.of(page, pageSize, Sort.by("createdAt").descending()))
                : iUserAccountLogRepository.findAllByActionAndCreatedAtBetween(dateFrom, dateTo, type, PageRequest.of(page, pageSize, Sort.by("createdAt").descending()));
    }

    @Override
    public Object getActivities(int page, int size, String type, String fromDate, String toDate) {
        var dateFrom = DatetimeUtils.getFromDate(fromDate);
        var dateTo = DatetimeUtils.getToDate(toDate);
        return getListActivity(page, size, type, null, dateFrom, dateTo);
    }

    @Override
    public LoginDTO readCaptcha(HttpServletRequest request) {
        try {
            return new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
        } catch (IOException e) {
            log.log(Level.WARNING, "LoginAuthenticationFilter >> readRequest >> IOException: ", e.getMessage());
            throw new AuthenticationServiceException("AUTHENTICATION.INVALID_REQUEST");
        }
    }

    @Override
    public boolean isValidCaptcha(HttpServletRequest request, String captcha) {
        var cookie = CookieUtils.get(request, Constant.CAPTCHA);
        return cookie.isPresent() && AESUtils.decrypt(cookie.get().getValue()).equals(captcha.trim());
    }

    public String randomPassword() {
        return UUID
                .randomUUID()
                .toString();
    }
}
