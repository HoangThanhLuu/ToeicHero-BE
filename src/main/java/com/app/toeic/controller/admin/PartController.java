package com.app.toeic.controller.admin;


import com.app.toeic.dto.PartDto;
import com.app.toeic.response.ResponseVO;
import com.app.toeic.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/part")
public class PartController {

    private final PartService partService;

    @PostMapping("/list-by-exam")
    public ResponseVO getAllPartByExamId(@RequestBody PartDto partDto) {
        var parts = partService.getAllPartByExamId(partDto.getExamId());
        return ResponseVO.builder()
                .success(Boolean.TRUE)
                .data(parts)
                .message("Lấy danh sách part thành công!")
                .build();
    }

    @GetMapping("/find-by-id")
    public ResponseVO getPartById(@RequestParam("partId") Integer partId) {
        var part = partService.getPartById(partId);
        return ResponseVO.builder()
                .success(Boolean.TRUE)
                .data(part)
                .message("Lấy part thành công!")
                .build();
    }
}