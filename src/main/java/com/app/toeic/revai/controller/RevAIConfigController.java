package com.app.toeic.revai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/revai/config")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RevAIConfigController {
}
