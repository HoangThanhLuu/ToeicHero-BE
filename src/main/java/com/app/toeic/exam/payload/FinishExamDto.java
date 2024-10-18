package com.app.toeic.exam.payload;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class FinishExamDto {
    Integer examId;
    List<Answer> answers;
    Integer totalTime;
    Integer timeRemaining;
    Integer totalQuestion;
    Boolean isFullTest;
    Boolean isDone;
    String listPart;

    @Getter
    @Setter
    public static class Answer {
        Integer questionId;
        String answer;
        String partCode;
    }
}

