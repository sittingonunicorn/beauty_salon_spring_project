package net.ukr.lina_chen.beauty_salon_spring_project.service;

import lombok.NonNull;
import net.ukr.lina_chen.beauty_salon_spring_project.entity.Record;
import net.ukr.lina_chen.beauty_salon_spring_project.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordService {

    private RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public List<Record> findRecordsByMasterId(@NonNull Long masterId){
       return recordRepository.findRecordsByMasterId(masterId);
    }
}
