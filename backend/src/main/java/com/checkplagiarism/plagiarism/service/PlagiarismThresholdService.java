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
        pl.setColor(req.getColor());
        pl.setDefault(false);

        
        if (req.getClassId()!=null) {
            ClassRoom classRoom= this.classRoomRepository.findById(req.getClassId()).orElse(null);
            pl.setClassRoom(classRoom);
        }

        return this.plagiarismThresholdRepository.save(pl);
    }

    public PlagiarismThresholds update(ReqUpdateThresholdDTO req) {
        PlagiarismThresholds pl = new PlagiarismThresholds();
        pl.setLevelName(req.getLevelName());
        pl.setDescription(req.getDescription());
        pl.setMax(req.getMax());
        pl.setMin(req.getMin());
        pl.setColor(req.getColor());

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
}
