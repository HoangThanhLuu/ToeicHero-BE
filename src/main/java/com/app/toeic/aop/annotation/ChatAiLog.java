package com.app.toeic.aop.annotation;

import com.app.toeic.chatai.model.ModelChat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatAiLog {
    ModelChat model() default ModelChat.GEMINI;
}
