package com.example.atc.domain.trashBin.controller;

import com.example.atc.domain.trashBin.dto.TrashBinDto;
import com.example.atc.domain.trashBin.entity.TrashBin;
import com.example.atc.domain.trashBin.service.TrashBinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/trashbins")
public class TrashBinController {

    private final TrashBinService trashBinService;

    public TrashBinController(TrashBinService trashBinService) {
        this.trashBinService = trashBinService;
    }

    // CSV 파일의 경로를 받아서 쓰레기통 데이터를 DB에 저장
    /*
    @PostMapping("/loadFromCsv")
    public ResponseEntity<String> loadTrashBinsFromCsv(@RequestParam("filePath") String filePath) {
        try {
            trashBinService.saveTrashBinsFromCsv(filePath);
            return ResponseEntity.ok("CSV 파일로부터 쓰레기통 데이터가 성공적으로 저장되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 처리 중 오류 발생: " + e.getMessage());
        }
    }
     */
    @PostMapping("/loadFromCsv")
    public ResponseEntity<String> loadTrashBinsFromCsv(@RequestParam("file") MultipartFile file) {
        try {
            // 파일을 임시 경로에 저장
            Path tempFile = Files.createTempFile("uploaded_", ".csv");
            file.transferTo(tempFile.toFile());

            // 임시 파일 경로로 서비스 호출
            trashBinService.saveTrashBinsFromCsv(tempFile.toString());

            // 처리 후 임시 파일 삭제
            Files.delete(tempFile);

            return ResponseEntity.ok("CSV 파일로부터 쓰레기통 데이터가 성공적으로 저장되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 처리 중 오류 발생: " + e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<TrashBin> createTrashBin(@RequestBody TrashBinDto trashBinDTO) {
        TrashBin newTrashBin = new TrashBin(trashBinDTO.getLatitude(), trashBinDTO.getLongitude());
        return ResponseEntity.ok(trashBinService.saveTrashBin(newTrashBin));
    }

    @GetMapping
    public ResponseEntity<List<TrashBin>> getAllTrashBins() {
        return ResponseEntity.ok(trashBinService.getAllTrashBins());
    }

    // 범위 내 쓰레기통 데이터를 조회
    @PostMapping("/findBetween")
    public ResponseEntity<List<TrashBin>> findTrashBinsBetween(
            @RequestParam double startLatitude,
            @RequestParam double startLongitude,
            @RequestParam double endLatitude,
            @RequestParam double endLongitude) {

        System.out.println("startLatitude: " + startLatitude + ", startLongitude: " + startLongitude);
        System.out.println("endLatitude: " + endLatitude + ", endLongitude: " + endLongitude);

        List<TrashBin> bins = trashBinService.findTrashBinsWithinRange(startLatitude, startLongitude, endLatitude, endLongitude);
        return ResponseEntity.ok(bins);

    }
}
