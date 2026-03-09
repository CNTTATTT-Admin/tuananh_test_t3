package com.checkplagiarism.plagiarism.domain;

import java.time.Instant;
import java.util.List;

import com.checkplagiarism.plagiarism.util.constants.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @OneToMany(mappedBy = "role",fetch =  FetchType.LAZY)
    @JsonIgnore
    List<User> user;
}
