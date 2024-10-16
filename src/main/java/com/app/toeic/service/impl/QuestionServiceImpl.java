package com.app.toeic.service.impl;

import com.app.toeic.exception.AppException;
import com.app.toeic.model.Part;
import com.app.toeic.model.Question;
import com.app.toeic.repository.IQuestionRepository;
import com.app.toeic.response.ResponseVO;
import com.app.toeic.service.QuestionService;
import com.app.toeic.util.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


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

    @Override
    public Question findById(Integer id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy câu hỏi!"));
    }

    @Override
    public void saveQuestion(Question question) {
        questionRepository.save(question);
    }
}







