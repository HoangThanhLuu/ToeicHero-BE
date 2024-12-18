package com.app.toeic.external.service;

import com.app.toeic.part.model.Part;
import com.app.toeic.question.model.Question;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExcelService {
    List<Question> excelToPart1(InputStream is, Part part, List<Question> list, boolean isAddNew) throws IOException;

    List<Question> excelToPart2(InputStream is, Part part, List<Question> list, boolean isAddNew) throws IOException;

    List<Question> excelToPart3(InputStream is, Part part, List<Question> list, boolean isAddNew) throws IOException;

    List<Question> excelToPart4(InputStream is, Part part, List<Question> list, boolean isAddNew) throws IOException;

    List<Question> excelToPart5(InputStream is, Part part, List<Question> list, boolean isAddNew) throws IOException;

    List<Question> excelToPart6(InputStream is, Part part, List<Question> list, boolean isAddNew) throws IOException;

    List<Question> excelToPart7(InputStream is, Part part, List<Question> list, boolean isAddNew) throws IOException;
}
