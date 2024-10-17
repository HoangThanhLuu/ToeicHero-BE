package com.app.toeic.service;

import com.app.toeic.dto.EmailDto;
import com.app.toeic.dto.LoginSocialDto;
import com.app.toeic.response.ResponseVO;
import org.thymeleaf.context.Context;

public interface EmailService {
    String generateOTP();

    ResponseVO sendEmail(EmailDto emailDto, String templateName);

    Object sendEmailAccount(LoginSocialDto loginSocialDto, String password, String templateName);
}
