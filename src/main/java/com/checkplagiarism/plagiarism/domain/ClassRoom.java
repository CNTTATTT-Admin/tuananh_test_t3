package com.checkplagiarism.plagiarism.domain;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "classes")
@Entity
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(unique = true)
    private String classCode;
    

    @OneToMany(mappedBy = "classRoom",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClassStudent> classStudents;

    @OneToMany(mappedBy = "classRoom",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Assignment> assignments;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private User lecturer;

    //    @OneToMany(mappedBy = "classRoom",fetch = FetchType.LAZY)
    // @JsonIgnore
    // private List<Submission> submissions;

    @PrePersist
    public void handleBefore(){
        this.classCode="CLASS-"+RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }
}
