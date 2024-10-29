package com.app.toeic.transcript.controller;


import com.app.toeic.external.response.ResponseVO;
import com.app.toeic.firebase.repo.FirebaseRepository;
import com.app.toeic.firebase.service.FirebaseStorageService;
import com.app.toeic.revai.repo.RevAIConfigRepo;
import com.app.toeic.transcript.model.TranscriptHistory;
import com.app.toeic.transcript.repo.TranscriptRepo;
import com.app.toeic.transcript.service.RevAITranscriptService;
import com.app.toeic.translate.service.TranslateService;
import com.app.toeic.util.DatetimeUtils;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ai.rev.speechtotext.ApiClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.logging.Level;

@Log
@RestController
@RequestMapping("transcript")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TranscriptController {
    RevAIConfigRepo revAIConfigRepo;
    FirebaseRepository firebaseRepository;
    TranscriptRepo transcriptRepo;
    FirebaseStorageService firebaseStorageService;
    RevAITranscriptService revAITranscriptService;
    TranslateService translateService;

    @PostMapping(value = "get/google", consumes = {"multipart/form-data"})
    public Object getTranscript(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "name") String name
    ) throws IOException {
        // check file is mp3
        if (!Objects.requireNonNull(file.getContentType()).startsWith("audio/")) {
            return ResponseVO.builder()
                    .data(null)
                    .success(false)
                    .message("FILE_NOT_SUPPORT")
                    .build();
        }

        var firebaseConfig = firebaseRepository.findAllByStatus(true).stream().findFirst();
        if (firebaseConfig.isEmpty()) {
            return ResponseVO
                    .builder()
                    .data(null)
                    .success(true)
                    .message("FIREBASE_CONFIG_NOT_FOUND")
                    .build();
        }
        var rs = new StringBuilder();
        var jsonContent = firebaseConfig.get().getFileJson();
        var credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(jsonContent.getBytes()));
        try (SpeechClient speechClient = SpeechClient.create(
                SpeechSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build()
        )) {
            RecognitionAudio recognitionAudio =
                    RecognitionAudio
                            .newBuilder()
                            .setContent(ByteString.copyFrom(file.getBytes()))
                            .build();
            var audioEncoding = Objects.requireNonNull(file.getContentType()).startsWith("audio/")
                    ? RecognitionConfig.AudioEncoding.MP3
                    : RecognitionConfig.AudioEncoding.LINEAR16;
            var config =
                    RecognitionConfig
                            .newBuilder()
                            .setEncoding(audioEncoding)
                            .setSampleRateHertz(16000)
                            .setLanguageCode("en-US")
                            .build();
            var recognizeResponse = speechClient.longRunningRecognizeAsync(config, recognitionAudio);

            var results = recognizeResponse.get().getResultsList();
            for (var result : results) {
                var alternative = result.getAlternativesList().getFirst();
                rs.append(alternative.getTranscript());
            }
            //            var translate = translateService.translate(rs.toString());

            return ResponseVO
                    .builder()
                    .data(rs.toString())
                    .success(true)
                    .message("TRANSCRIPT_SUCCESS")
                    .build();
        } catch (Exception e) {
            log.log(Level.WARNING, "TranscriptController >> getTranscript >> error: {}", e);
            return ResponseVO
                    .builder()
                    .data(e.getMessage())
                    .success(false)
                    .message("TRANSCRIPT_FAILED")
                    .build();
        }
    }


    @PostMapping(value = "get/revai", consumes = {"multipart/form-data"})
    public Object getTranscriptV2(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "name") String name
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseVO.builder()
                    .data(null)
                    .success(false)
                    .message("FILE_NOT_PRESENT")
                    .build();
        }

        if (!Objects.requireNonNull(file.getContentType()).startsWith("audio/")) {
            return ResponseVO.builder()
                    .data(null)
                    .success(false)
                    .message("FILE_NOT_SUPPORT")
                    .build();
        }
        var fileUrl = firebaseStorageService.uploadFile(file);

        var transcriptHistory = TranscriptHistory
                .builder()
                .transcriptName(name)
                .transcriptAudio(fileUrl)
                .build();
        var transcriptHistory1 = transcriptRepo.save(transcriptHistory);
        revAITranscriptService.getTranscript(fileUrl, transcriptHistory1);
        return ResponseVO.builder()
                .success(true)
                .message("TRANSCRIPT_SUCCESS")
                .build();
    }

    @GetMapping("get-revai-job")
    public Object getRevAIJob(@RequestParam(value = "jobId") String jobId) throws IOException {
        var revAiConfig = revAIConfigRepo.findAllByStatus(true);
        if (CollectionUtils.isEmpty(revAiConfig) || StringUtils.isBlank(revAiConfig.getFirst().getAccessToken())) {
            return ResponseVO.builder()
                    .data(null)
                    .success(false)
                    .message("REV_AI_CONFIG_NOT_FOUND")
                    .build();
        }

        var apiClient = new ApiClient(revAiConfig.getFirst().getAccessToken());
        var revAiJob = apiClient.getJobDetails(jobId);
        return ResponseVO.builder()
                .data(revAiJob)
                .success(true)
                .message("GET_REV_AI_JOB_SUCCESS")
                .build();
    }

    @GetMapping("get-transcript-revai")
    public Object getTrancriptRevai(@RequestParam(value = "jobId") String jobId) throws IOException {
        var revAiConfig = revAIConfigRepo.findAllByStatus(true);
        if (CollectionUtils.isEmpty(revAiConfig) || StringUtils.isBlank(revAiConfig.getFirst().getAccessToken())) {
            return ResponseVO.builder()
                    .data(null)
                    .success(false)
                    .message("REV_AI_CONFIG_NOT_FOUND")
                    .build();
        }

        var apiClient = new ApiClient(revAiConfig.getFirst().getAccessToken());
        var transcript = apiClient.getTranscriptText(jobId);
        return ResponseVO.builder()
                .data(transcript)
                .success(true)
                .message("GET_REV_AI_JOB_SUCCESS")
                .build();
    }

    @GetMapping("/history")
    public Object getTranscriptHistory(
            @RequestParam(value = "dateFrom", defaultValue = "") String dateFrom,
            @RequestParam(value = "dateTo", defaultValue = "") String dateTo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "sort", defaultValue = "desc") String sort
    ) {
        log.log(Level.INFO, MessageFormat.format("TranscriptController >> getTranscriptHistory >> dateFrom: {0}, dateTo: {1}, page: {2}, size: {3}", dateFrom, dateTo, page, size));
        var startDateTime = DatetimeUtils.getFromDate(dateFrom);
        var endDateTime = DatetimeUtils.getToDate(dateTo);
        var sortRequest = "asc".equalsIgnoreCase(sort)
                ? Sort.by("createdAt").ascending()
                : Sort.by("createdAt").descending();
        var pageRequest = PageRequest.of(page, size, sortRequest);
        var result = "all".equalsIgnoreCase(status)
                ? transcriptRepo.findAllByCreatedAtBetween(startDateTime, endDateTime, pageRequest)
                : transcriptRepo.findAllByCreatedAtBetweenAndStatus(startDateTime, endDateTime, status, pageRequest);
        return ResponseVO.builder()
                .data(result)
                .success(true)
                .message("GET_TRANSCRIPT_HISTORY_SUCCESS")
                .build();
    }

    @GetMapping("translate/{id}")
    public Object getTranslate(@PathVariable("id") Long id) {
        var transcriptHistory = transcriptRepo.findById(id);
        final var msg = new String[1];
        final var success = new Boolean[1];
        success[0] = false;
        transcriptHistory.ifPresentOrElse(e -> {
            if (StringUtils.isBlank(e.getTranscriptContent())) {
                msg[0] = "TRANSCRIPT_NOT_FOUND";
            } else {
                var translate = translateService.translate(e.getTranscriptContent());
                e.setTranscriptContentTranslate(translate.toString());
                transcriptRepo.save(e);
                msg[0] = "TRANSLATE_SUCCESS";
                success[0] = true;
            }
        }, () -> msg[0] = "TRANSCRIPT_NOT_FOUND");
        return ResponseVO.builder()
                .success(success[0])
                .message(msg[0])
                .build();
    }


}
