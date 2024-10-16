package com.app.toeic.service.impl;

import com.app.toeic.model.Part;
import com.app.toeic.model.Question;
import com.app.toeic.repository.IQuestionRepository;
import com.app.toeic.response.ResponseVO;
import com.app.toeic.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final IQuestionRepository questionRepository;

    @Override
    @Transactional
    public void saveAllQuestion(List<Question> list) {
        questionRepository.saveAll(list);
    }

    @Override
    public ResponseVO getAllQuestionByPartId(Part part) {
        return ResponseVO
                .builder()
                .success(Boolean.TRUE)
                .data(questionRepository.findAllByPart(part))
                .message("Lấy danh sách câu hỏi thành công!")
                .build();
    }

    public List<Question> getAllQuestionByPart(Part part) {
        return questionRepository.findAllByPart(part);
    }

    @Override
    @Transactional
    public void removeQuestionByPart(Part part) {
        questionRepository.deleteAllByPart(part);
    }
}
