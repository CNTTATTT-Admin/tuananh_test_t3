package com.checkplagiarism.plagiarism.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.ClassRoom;
import com.checkplagiarism.plagiarism.domain.PlagiarismThresholds;
import com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold.ReqCreateThresholdDTO;
import com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold.ReqSaveAllThresholdDTO;
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

    public PlagiarismThresholds create(ReqCreateThresholdDTO req) {
        if (req.getClassId() != null) {
            validateOverlap(req.getClassId(), null, req.getMin(), req.getMax(), req.getLevelName());
        }

        PlagiarismThresholds pl = new PlagiarismThresholds();
        pl.setLevelName(req.getLevelName());
        pl.setDescription(req.getDescription());
        pl.setMax(req.getMax());
        pl.setMin(req.getMin());
        pl.setColor(req.getColor());
        pl.setDefault(false);

        if (req.getClassId() != null) {
            ClassRoom classRoom = this.classRoomRepository.findById(req.getClassId()).orElse(null);
            pl.setClassRoom(classRoom);
        }

        return this.plagiarismThresholdRepository.save(pl);
    }

    public PlagiarismThresholds update(ReqUpdateThresholdDTO req) {
        PlagiarismThresholds current = this.plagiarismThresholdRepository.findById(req.getId()).orElse(null);
        if (current == null) {
            return null;
        }

        if (current.getClassRoom() != null) {
            validateOverlap(current.getClassRoom().getId(), req.getId(), req.getMin(), req.getMax(), req.getLevelName());
        }

        current.setLevelName(req.getLevelName());
        current.setDescription(req.getDescription());
        current.setMax(req.getMax());
        current.setMin(req.getMin());
        current.setColor(req.getColor());

        return this.plagiarismThresholdRepository.save(current);
    }

    private void validateOverlap(Long classId, Long excludeId, int min, int max, String name) {
        if (min < 0 || max > 100 || min > max) {
            throw new RuntimeException("Phạm vi tỷ lệ không hợp lệ (0-100) và min phải nhỏ hơn hoặc bằng max.");
        }

        List<PlagiarismThresholds> existing = this.plagiarismThresholdRepository.findByClassRoomId(classId);
        for (PlagiarismThresholds th : existing) {
            if (excludeId != null && th.getId().equals(excludeId)) continue;

            // Check duplicate name
            if (th.getLevelName().equalsIgnoreCase(name)) {
                throw new RuntimeException("Tên mức độ '" + name + "' đã tồn tại.");
            }

            // Check overlap
            // Range A (min, max) overlaps with Range B (th.min, th.max) if:
            // (min <= th.max) AND (max >= th.min)
            if (min <= th.getMax() && max >= th.getMin()) {
                throw new RuntimeException("Phạm vi " + min + "-" + max + "% bị chồng chéo với mức '" + th.getLevelName() + "' (" + th.getMin() + "-" + th.getMax() + "%).");
            }
        }
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

    @jakarta.transaction.Transactional
    public List<PlagiarismThresholds> saveAll(ReqSaveAllThresholdDTO req) {
        Long classId = req.getClassId();
        ClassRoom classRoom = this.classRoomRepository.findById(classId).orElseThrow(() -> new RuntimeException("Lớp học không tồn tại."));

        // 1. Validate the whole list for overlaps and duplicate names
        List<ReqSaveAllThresholdDTO.ThresholdItem> items = req.getThresholds();
        List<ReqSaveAllThresholdDTO.ThresholdItem> sorted = items.stream()
                .sorted((a, b) -> Integer.compare(a.getMin(), b.getMin()))
                .collect(Collectors.toList());

        for (int i = 0; i < sorted.size(); i++) {
            ReqSaveAllThresholdDTO.ThresholdItem current = sorted.get(i);
            
            if (current.getMin() < 0 || current.getMax() > 100 || current.getMin() > current.getMax()) {
                throw new RuntimeException("Mức '" + current.getLevelName() + "' có phạm vi không hợp lệ (" + current.getMin() + "-" + current.getMax() + "%).");
            }

            if (i < sorted.size() - 1) {
                ReqSaveAllThresholdDTO.ThresholdItem next = sorted.get(i+1);
                if (current.getMax() >= next.getMin()) {
                    throw new RuntimeException("Mức '" + current.getLevelName() + "' và '" + next.getLevelName() + "' bị chồng chéo.");
                }
            }
        }

        // Check for duplicate names
        long distinctNames = items.stream().map(i -> i.getLevelName().toUpperCase()).distinct().count();
        if (distinctNames < items.size()) {
            throw new RuntimeException("Tên các mức độ không được trùng nhau.");
        }

        // 2. Delete existing thresholds for this class
        List<PlagiarismThresholds> existing = this.plagiarismThresholdRepository.findByClassRoomId(classId);
        this.plagiarismThresholdRepository.deleteAll(existing);

        // 3. Save new thresholds
        List<PlagiarismThresholds> toSave = items.stream().map(item -> {
            PlagiarismThresholds th = new PlagiarismThresholds();
            th.setLevelName(item.getLevelName());
            th.setMin(item.getMin());
            th.setMax(item.getMax());
            th.setColor(item.getColor());
            th.setDescription(item.getDescription());
            th.setClassRoom(classRoom);
            th.setDefault(false);
            return th;
        }).collect(Collectors.toList());

        return this.plagiarismThresholdRepository.saveAll(toSave);
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
}
