package com.checkplagiarism.plagiarism.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.ClassRoom;
import com.checkplagiarism.plagiarism.domain.PlagiarismThresholds;
import com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold.ReqCreateThresholdDTO;
import com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold.ReqUpdateThresholdDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.repository.ClassRoomRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismThresholdRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlagiarismThresholdService {
    private final PlagiarismThresholdRepository plagiarismThresholdRepository;
    private final ClassRoomRepository classRoomRepository; 

    public PlagiarismThresholds create(ReqCreateThresholdDTO req){
        PlagiarismThresholds pl= new PlagiarismThresholds();
        pl.setLevelName(req.getLevelName());
        pl.setDescription(req.getDescription());
        pl.setMax(req.getMax());
        pl.setMin(req.getMin());
        if (this.plagiarismThresholdRepository.findAll().size()>4) {
            
            pl.setDefault(false);
        }
        pl.setDefault(true);

        
        if (req.getClassId()!=null) {
            ClassRoom classRoom= this.classRoomRepository.findById(req.getClassId()).orElse(null);
            pl.setClassRoom(classRoom);
        }

        return this.plagiarismThresholdRepository.save(pl);
    }

    public PlagiarismThresholds update(ReqUpdateThresholdDTO req) {
        PlagiarismThresholds pl = this.plagiarismThresholdRepository.getById(req.getId());
        if (pl!=null) {
            
            pl.setLevelName(req.getLevelName());
            pl.setDescription(req.getDescription());
            pl.setMax(req.getMax());
            pl.setMin(req.getMin());
        }

        if (req.getClassId() != null) {
            ClassRoom classRoom = this.classRoomRepository.findById(req.getClassId()).orElse(null);
            pl.setClassRoom(classRoom);
        }
        pl= this.plagiarismThresholdRepository.save(pl);

        return pl;
    }

    public PlagiarismThresholds getById(Long id){
        return this.plagiarismThresholdRepository.findById(id).orElse(null);
    }

        public ResultPaginationDTO handleGetAll(Specification<PlagiarismThresholds> spec, Pageable page){
        Page<PlagiarismThresholds> thres= this.plagiarismThresholdRepository.findAll(spec, page);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(thres.getNumber() + 1);
        meta.setPageSize(thres.getSize());
        meta.setPages(thres.getTotalPages());
        meta.setTotal(thres.getTotalElements());

        rs.setMeta(meta);
        rs.setResults(thres.getContent());
        return rs;
    }

    public void delete(Long id){
        this.plagiarismThresholdRepository.deleteById(id);
    }

    public List<PlagiarismThresholds> getThresholdByClass(Long classId) {

        List<PlagiarismThresholds> thresholds = this.plagiarismThresholdRepository.findByClassRoomId(classId);

        if (thresholds.isEmpty()) {
            thresholds = this.plagiarismThresholdRepository.findByIsDefaultTrue();
        }

        return thresholds;
    }

    public void setDefault(Long classId){
        List<PlagiarismThresholds> thresholds = this.plagiarismThresholdRepository.findByClassRoomId(classId);
        for(PlagiarismThresholds pl:thresholds){
            pl.setDefault(true);
            this.plagiarismThresholdRepository.save(pl);
        }
    }

    public void addLevel(Long classId) {

        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        List<PlagiarismThresholds> list = plagiarismThresholdRepository
                .findByClassRoomIdOrderByMaxAsc(classId);

        int min = 0;
        int max = 20;

        String levelName = "SAFE";

        if (!list.isEmpty()) {

            PlagiarismThresholds last = list.get(list.size() - 1);

            if (last.getMax() >= 100) {
                throw new RuntimeException("Max level reached");
            }

            min = last.getMax();
            max = min + 20;

            if (max > 100)
                max = 100;

            switch (list.size()) {
                case 1:
                    levelName = "LOW";
                    break;
                case 2:
                    levelName = "MODERATE";
                    break;
                case 3:
                    levelName = "HIGH";
                    break;
                case 4:
                    levelName = "SEVERE";
                    break;
            }
        }

        PlagiarismThresholds threshold = new PlagiarismThresholds();
        threshold.setMin(min);
        threshold.setMax(max);
        threshold.setLevelName(levelName);
        threshold.setClassRoom(classRoom);

        plagiarismThresholdRepository.save(threshold);
    }

    public PlagiarismThresholds updateLevel(Long id, Integer min, Integer max) {

        PlagiarismThresholds level = plagiarismThresholdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Threshold not found"));

        List<PlagiarismThresholds> list = plagiarismThresholdRepository
                .findByClassRoomIdOrderByMinAsc(level.getClassRoom().getId());

        if (min >= max) {
            throw new RuntimeException("Min must be smaller than Max");
        }

        if (min < 0 || max > 100) {
            throw new RuntimeException("Range must be between 0 and 100");
        }

        for (PlagiarismThresholds t : list) {

            if (t.getId().equals(id))
                continue;

            boolean overlap = min < t.getMax() && max > t.getMin();

            if (overlap) {
                throw new RuntimeException("Threshold overlaps with existing level");
            }
        }

        level.setMin(min);
        level.setMax(max);

        return plagiarismThresholdRepository.save(level);
    }
}
