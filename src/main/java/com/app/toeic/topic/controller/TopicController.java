package com.app.toeic.topic.controller;


import com.app.toeic.external.response.ResponseVO;
import com.app.toeic.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/topic")
@RequiredArgsConstructor
@RestController
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TopicController {
    TopicService topicService;

    @GetMapping("/list")
    public ResponseVO getAllTopics() {
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(topicService.getAllTopics())
                .message("GET_ALL_TOPIC_SUCCESS")
                .build();
    }
}
