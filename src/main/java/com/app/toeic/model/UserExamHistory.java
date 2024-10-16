package com.app.toeic.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "user_exam_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserExamHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userExamHistoryId;

    private int numberOfCorrectAnswer;
    private int numberOfWrongAnswer;
    private int numberOfNotAnswer;
    private int numberOfCorrectAnswerPart1;
    private int numberOfCorrectAnswerPart2;
    private int numberOfCorrectAnswerPart3;
    private int numberOfCorrectAnswerPart4;
    private int numberOfCorrectAnswerPart5;
    private int numberOfCorrectAnswerPart6;
    private int numberOfCorrectAnswerPart7;
    private int numberOfCorrectListeningAnswer;
    private int numberOfWrongListeningAnswer;
    private int numberOfCorrectReadingAnswer;
    private int numberOfWrongReadingAnswer;
    private int totalScore;

    @CreationTimestamp
    private LocalDateTime examDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Exam exam;
}