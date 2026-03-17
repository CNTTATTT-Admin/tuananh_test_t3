package com.checkplagiarism.plagiarism.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkplagiarism.plagiarism.domain.PlagiarismThresholds;
import com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold.ReqCreateThresholdDTO;
import com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold.ReqUpdateThresholdDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.service.PlagiarismThresholdService;
import com.checkplagiarism.plagiarism.util.err.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ThresholdController {
    private final PlagiarismThresholdService service;

    @PostMapping("/thresholds")
    public ResponseEntity<PlagiarismThresholds> create(@RequestBody ReqCreateThresholdDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(req));
    }

    @PutMapping("/thresholds")
    public ResponseEntity<PlagiarismThresholds> update(@RequestBody ReqUpdateThresholdDTO req) throws IdInvalidException {
        PlagiarismThresholds pl= this.service.getById(req.getId());
        if (pl==null) {
            throw new IdInvalidException("threshold not found");
        }
        return ResponseEntity.ok().body(this.service.update(req));
    }

    @GetMapping("/thresholds/{id}")
    public ResponseEntity<PlagiarismThresholds> getById(@PathVariable Long id) throws IdInvalidException {
        PlagiarismThresholds pl = this.service.getById(id);
        if (pl == null) {
            throw new IdInvalidException("threshold not found");
        }
        return ResponseEntity.ok().body(pl);
    }
       
    @GetMapping("/thresholds")
    public ResponseEntity<ResultPaginationDTO> getAllThreshold(@Filter Specification<PlagiarismThresholds> spec,Pageable page) {
        return ResponseEntity.ok().body(this.service.handleGetAll(spec, page));
    } 

    @DeleteMapping("/thresholds/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) throws IdInvalidException {
        PlagiarismThresholds pl = this.service.getById(id);
        if (pl == null) {
            throw new IdInvalidException("threshold not found");
        }
        this.service.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping("/thresholds/default")
    public ResponseEntity<?> setDefault(@RequestBody Long classId) {
        this.service.setDefault(classId);
        return ResponseEntity.ok().body("set default success");
    }

    @GetMapping("/thresholds/class/{classId}")
    public ResponseEntity<List<PlagiarismThresholds>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok().body(this.service.getThresholdByClass(classId));
    }

    @PostMapping("/thresholds/class/{classId}")
    public ResponseEntity<?> addThreshold(@PathVariable Long classId) {

        this.service.addLevel(classId);

        return ResponseEntity.ok("Added threshold");
    }

    @PutMapping("/thresholds/class")
    public ResponseEntity<?> updateThreshold(
            @PathVariable Long id,
            @RequestBody ReqUpdateThresholdDTO req) {

        PlagiarismThresholds level = this.service.updateLevel(id, req.getMin(), req.getMax());

        return ResponseEntity.ok().body(level);
    }
    
}
