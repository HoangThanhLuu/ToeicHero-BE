package com.app.toeic.topic.response;


import java.time.LocalDateTime;

public interface TopicUserExam {
    Integer getTopicId();

    String getTopicName();
    String getExamName();
    LocalDateTime getExamDate();
}
