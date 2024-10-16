package com.app.toeic.service;

import com.app.toeic.model.Part;
import com.app.toeic.model.Question;
import com.app.toeic.response.ResponseVO;

import java.util.List;

public interface QuestionService {
    void saveAllQuestion(List<Question> list);

    ResponseVO getAllQuestionByPartId(Part part);
    List<Question> getAllQuestionByPart(Part part);
    void removeQuestionByPart(Part part);
}
