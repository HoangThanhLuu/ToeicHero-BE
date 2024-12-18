package com.app.toeic.external.service.impl;

import com.app.toeic.external.service.ExcelService;
import com.app.toeic.exception.AppException;
import com.app.toeic.part.model.Part;
import com.app.toeic.question.model.Question;
import com.app.toeic.util.Constant;
import com.app.toeic.util.HttpStatus;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

@Service
public class ExcelServiceImpl implements ExcelService {
    private static final String NAME_SHEET = "Sheet1";
    private static final String EXCEL_FILE_NOT_CORRECT_FORMAT = "EXCEL_FILE_NOT_CORRECT_FORMAT";

    @Override
    public List<Question> excelToPart1(InputStream is, Part part, List<Question> list, boolean isAddNew) throws
            IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet(NAME_SHEET);
        Iterator<Row> rows = sheet.iterator();
        if (sheet.getPhysicalNumberOfRows() - 1 != Constant.NUMBER_QUESTION_PART_1) {
            throw new AppException(HttpStatus.SEE_OTHER, EXCEL_FILE_NOT_CORRECT_FORMAT);
        }
        List<Question> questionList = isAddNew ? new ArrayList<>() : list;
        int rowNumber = 0;
        int questionNumber = 1;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            for (Cell currentCell : currentRow) {
                int columnIndex = currentCell.getColumnIndex();
                if (columnIndex == 10) {
                    if (isAddNew) {
                        var question = Question
                                .builder()
                                .questionNumber(questionNumber)
                                .correctAnswer(currentCell.getStringCellValue())
                                .part(part)
                                .build();
                        questionList.add(question);
                    } else {
                        var question = questionList.get(questionNumber - 1);
                        question.setCorrectAnswer(currentCell.getStringCellValue());
                    }
                }
            }
            questionNumber++;
        }
        workbook.close();
        return questionList;
    }

    @Override
    public List<Question> excelToPart2(InputStream is, Part part, List<Question> list, boolean isAddNew) throws
            IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet(NAME_SHEET);
        Iterator<Row> rows = sheet.iterator();
        if (sheet.getPhysicalNumberOfRows() - 1 != 25) {
            throw new AppException(HttpStatus.SEE_OTHER, EXCEL_FILE_NOT_CORRECT_FORMAT);
        }
        List<Question> questionList = isAddNew ? new ArrayList<>() : list;
        int rowNumber = 0;
        int questionNumber = 7;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            for (Cell currentCell : currentRow) {
                int columnIndex = currentCell.getColumnIndex();
                if (columnIndex == 10) {
                    if (isAddNew) {
                        var question = Question
                                .builder()
                                .questionNumber(questionNumber)
                                .correctAnswer(currentCell.getStringCellValue())
                                .part(part)
                                .build();
                        questionList.add(question);
                    } else {
                        var question = questionList.get(rowNumber - 1);
                        question.setCorrectAnswer(currentCell.getStringCellValue());
                    }
                }
            }
            questionNumber++;
            rowNumber++;
        }
        workbook.close();
        return questionList;
    }

    @Override
    public List<Question> excelToPart3(InputStream is, Part part, List<Question> list, boolean isAddNew) throws
            IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet(NAME_SHEET);
        Iterator<Row> rows = sheet.iterator();
        if (sheet.getPhysicalNumberOfRows() - 1 != 39) {
            throw new AppException(HttpStatus.SEE_OTHER, EXCEL_FILE_NOT_CORRECT_FORMAT);
        }
        List<Question> questionList = isAddNew ? new ArrayList<>() : list;
        int rowNumber = 0;
        int questionNumber = 32;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            var question = isAddNew ? Question
                    .builder()
                    .questionNumber(questionNumber)
                    .part(part)
                    .build() : questionList.get(rowNumber - 1);
            for (Cell currentCell : currentRow) {
                int columnIndex = currentCell.getColumnIndex();
                var cellType = currentCell.getCellType();
                String value;
                if (cellType == CellType.STRING) {
                    value = currentCell.getStringCellValue();
                } else {
                    value = String.valueOf(currentCell.getNumericCellValue());
                }
                switch (columnIndex) {
                    case 1 -> question.setQuestionContent(value);
                    case 4 -> question.setAnswerA(value);
                    case 5 -> question.setAnswerB(value);
                    case 6 -> question.setAnswerC(value);
                    case 7 -> question.setAnswerD(value);
                    case 10 -> question.setCorrectAnswer(value);
                    default -> {
                        break;
                    }
                }
            }
            if (isAddNew) {
                questionList.add(question);
            }
            questionNumber++;
            rowNumber++;
        }
        workbook.close();
        return questionList;
    }

    @Override
    public List<Question> excelToPart4(InputStream is, Part part, List<Question> list, boolean isAddNew) throws
            IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet(NAME_SHEET);
        Iterator<Row> rows = sheet.iterator();
        if (sheet.getPhysicalNumberOfRows() - 1 != 30) {
            throw new AppException(HttpStatus.SEE_OTHER, EXCEL_FILE_NOT_CORRECT_FORMAT);
        }
        List<Question> questionList = isAddNew ? new ArrayList<>() : list;
        int rowNumber = 0;
        int questionNumber = 71;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            var question = isAddNew ? Question
                    .builder()
                    .questionNumber(questionNumber)
                    .part(part)
                    .build() : questionList.get(rowNumber - 1);
            for (Cell currentCell : currentRow) {
                int columnIndex = currentCell.getColumnIndex();
                var cellType = currentCell.getCellType();
                String value;
                if (cellType == CellType.STRING) {
                    value = currentCell.getStringCellValue();
                } else {
                    value = String.valueOf(currentCell.getNumericCellValue());
                }
                switch (columnIndex) {
                    case 1 -> question.setQuestionContent(value);
                    case 4 -> question.setAnswerA(value);
                    case 5 -> question.setAnswerB(value);
                    case 6 -> question.setAnswerC(value);
                    case 7 -> question.setAnswerD(value);
                    case 10 -> question.setCorrectAnswer(value);
                    default -> {
                        break;
                    }
                }
            }
            if (isAddNew) {
                questionList.add(question);
            }
            questionNumber++;
            rowNumber++;
        }
        workbook.close();
        return questionList;
    }

    @Override
    public List<Question> excelToPart5(InputStream is, Part part, List<Question> list, boolean isAddNew) throws
            IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet(NAME_SHEET);
        Iterator<Row> rows = sheet.iterator();
        if (sheet.getPhysicalNumberOfRows() - 1 != 30) {
            throw new AppException(HttpStatus.SEE_OTHER, EXCEL_FILE_NOT_CORRECT_FORMAT);
        }
        List<Question> questionList = isAddNew ? new ArrayList<>() : list;
        int rowNumber = 0;
        int questionNumber = 101;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            var question = isAddNew ? Question
                    .builder()
                    .questionNumber(questionNumber)
                    .part(part)
                    .build() : questionList.get(rowNumber - 1);
            for (Cell currentCell : currentRow) {
                int columnIndex = currentCell.getColumnIndex();
                var cellType = currentCell.getCellType();
                String value;
                if (cellType == CellType.STRING) {
                    value = currentCell.getStringCellValue();
                } else {
                    value = String.valueOf(currentCell.getNumericCellValue());
                }
                switch (columnIndex) {
                    case 1 -> question.setQuestionContent(value);
                    case 4 -> question.setAnswerA(value);
                    case 5 -> question.setAnswerB(value);
                    case 6 -> question.setAnswerC(value);
                    case 7 -> question.setAnswerD(value);
                    case 10 -> question.setCorrectAnswer(value);
                    default -> {
                        break;
                    }
                }
            }
            if (isAddNew) {
                questionList.add(question);
            }
            questionNumber++;
            rowNumber++;
        }
        workbook.close();
        return questionList;
    }

    @Override
    public List<Question> excelToPart6(InputStream is, Part part, List<Question> list, boolean isAddNew) throws
            IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet(NAME_SHEET);
        Iterator<Row> rows = sheet.iterator();
        if (sheet.getPhysicalNumberOfRows() - 1 != 16) {
            throw new AppException(HttpStatus.SEE_OTHER, EXCEL_FILE_NOT_CORRECT_FORMAT);
        }
        List<Question> questionList = isAddNew ? new ArrayList<>() : list;
        int rowNumber = 0;
        int questionNumber = 131;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            var question = isAddNew ? Question
                    .builder()
                    .questionNumber(questionNumber)
                    .part(part)
                    .build() : questionList.get(rowNumber - 1);
            for (Cell currentCell : currentRow) {
                int columnIndex = currentCell.getColumnIndex();
                var cellType = currentCell.getCellType();
                String value;
                if (cellType == CellType.STRING) {
                    value = currentCell.getStringCellValue();
                } else {
                    value = String.valueOf(currentCell.getNumericCellValue());
                }
                switch (columnIndex) {
                    case 2 -> {
                        if (questionPart6HasParagraph(questionNumber)) {
                            question.setParagraph1(value);
                        }
                    }
                    case 4 -> question.setAnswerA(value);
                    case 5 -> question.setAnswerB(value);
                    case 6 -> question.setAnswerC(value);
                    case 7 -> question.setAnswerD(value);
                    case 10 -> question.setCorrectAnswer(value);
                    default -> {
                        break;
                    }
                }
            }
            if (isAddNew) {
                questionList.add(question);
            }
            questionNumber++;
            rowNumber++;
        }
        workbook.close();
        return questionList;
    }

    private boolean questionPart6HasParagraph(int questNumber) {
        return questNumber == 131 || questNumber == 135 || questNumber == 139 || questNumber == 143;
    }

    @Override
    public List<Question> excelToPart7(InputStream is, Part part, List<Question> list, boolean isAddNew) throws
            IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet(NAME_SHEET);
        Iterator<Row> rows = sheet.iterator();
        if (sheet.getPhysicalNumberOfRows() - 1 != 54) {
            throw new AppException(HttpStatus.SEE_OTHER, "File excel không đúng định dạng!");
        }
        List<Question> questionList = isAddNew ? new ArrayList<>() : list;
        int rowNumber = 0;
        int questionNumber = 147;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            var question = isAddNew ? Question
                    .builder()
                    .questionNumber(questionNumber)
                    .part(part)
                    .build() : questionList.get(rowNumber - 1);
            for (Cell currentCell : currentRow) {
                int columnIndex = currentCell.getColumnIndex();
                var cellType = currentCell.getCellType();
                String value;
                if (cellType == CellType.STRING) {
                    value = currentCell.getStringCellValue();
                } else {
                    value = String.valueOf(currentCell.getNumericCellValue());
                }
                switch (columnIndex) {
                    case 1 -> question.setQuestionContent(value);
                    case 2 -> question.setParagraph1(value);
                    case 3 -> question.setParagraph2(value);
                    case 4 -> question.setAnswerA(value);
                    case 5 -> question.setAnswerB(value);
                    case 6 -> question.setAnswerC(value);
                    case 7 -> question.setAnswerD(value);
                    case 10 -> question.setCorrectAnswer(value);
                    default -> {
                        break;
                    }
                }
            }
            if (isAddNew) {
                questionList.add(question);
            }
            questionNumber++;
            rowNumber++;
        }
        workbook.close();
        return questionList;
    }
}
