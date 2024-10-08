package com.example.atc.domain.pointRecord.service;

import com.example.atc.domain.pointRecord.dto.PointRecordDto;
import com.example.atc.domain.pointRecord.entity.PointRecord;
import com.example.atc.domain.pointRecord.repository.PointRecordRepository;
import com.example.atc.domain.user.entity.User;
import com.example.atc.domain.user.repository.UserRepository;
import com.example.atc.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointRecordService {

    private final PointRecordRepository pointRecordRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    public List<PointRecord> getAllPointRecords() {
        return pointRecordRepository.findAll();
    }

    public ResponseEntity<?> getPointRecordById(Long id) {
        Optional<PointRecord> pointRecord = pointRecordRepository.findById(id);
        if (pointRecord.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PointRecord not found with id " + id);
        }
        return ResponseEntity.ok(pointRecord.get());
    }

    public ResponseEntity<?> savePointRecord(PointRecordDto dto) {
        if(userService.getUserById(dto.getUserId()).getStatusCode() == HttpStatus.NOT_FOUND)
            return userService.getUserById(dto.getUserId());

        PointRecord pointRecord = new PointRecord();
        User user = (User) userService.getUserById(dto.getUserId()).getBody();

        List<PointRecord> todayTotalPoints = pointRecordRepository.findAll();
        int sum = 0;
        for (PointRecord pointRecordFor : todayTotalPoints) {
            if (isSameDay(pointRecordFor.getUsedDate(), LocalDateTime.now())) {
                sum += pointRecordFor.getAddSubPoint();
            }
        }


        pointRecord.setUser(user);
        pointRecord.setAddSubPoint(dto.getAddSubPoint());
        pointRecord.setTotalPoint(user.getTotalPoint() + dto.getAddSubPoint());
        pointRecord.setTodayTotalPoint(sum + dto.getAddSubPoint());
        pointRecord.setUsedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        user.setTotalPoint(pointRecord.getTotalPoint());
        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.OK).body(pointRecordRepository.save(pointRecord));
    }
    private boolean isSameDay(String usedDateStr, LocalDateTime currentDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate usedDate = LocalDate.parse(usedDateStr, formatter);
        LocalDate currentDate = currentDateTime.toLocalDate();

        return usedDate.getMonth() == currentDate.getMonth() && usedDate.getDayOfMonth() == currentDate.getDayOfMonth();
    }

    /*
    public ResponseEntity<?> updatePointRecord(Long id, PointRecordDto dto) {
        Optional<PointRecord> optionalPointRecord = pointRecordRepository.findById(id);
        if(optionalPointRecord.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PointRecord not found with id " + id);

        PointRecord pointRecord = optionalPointRecord.get();
        Optional<User> userOptional = userRepository.findById(id);
        User user;
        if(userOptional.isPresent()){
            user = userOptional.get();
        } else {
            return ResponseEntity.notFound().build();
        }

        //List<PointRecord> todayRecords = pointRecordRepository.findAllByUserAndUsedDate(user, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));


        List<PointRecord> todayTotalPoints = pointRecordRepository.findAll();
        int sum = 0;
        for (PointRecord pointRecordFor : todayTotalPoints){
            if (pointRecordFor.getUsedDate().equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                sum += pointRecordFor.getAddSubPoint();
            }
        }

        pointRecord.setAddSubPoint(dto.getAddSubPoint());
        pointRecord.setTotalPoint(user.getTotalPoint() + dto.getAddSubPoint());
        pointRecord.setTodayTotalPoint(sum + dto.getAddSubPoint());
        pointRecord.setUsedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        user.setTotalPoint(pointRecord.getTotalPoint());

        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.OK).body(pointRecordRepository.save(pointRecord));
    }
     */

    public ResponseEntity<?> deletePointRecord(Long id) {
        Optional<PointRecord> optionalPointRecord = pointRecordRepository.findById(id);
        if (optionalPointRecord.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PointRecord not found with id " + id);

        pointRecordRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("PointRecord deleted with id " + id);
    }
}
