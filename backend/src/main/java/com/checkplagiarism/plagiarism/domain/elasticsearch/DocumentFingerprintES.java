package com.checkplagiarism.plagiarism.domain.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(indexName = "fingerprints")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentFingerprintES {
    
    @Id
    private String id; // format: docId_hashValue

    @Field(type = FieldType.Long)
    private Long hashValue;

    @Field(type = FieldType.Long)
    private Long documentId;

    @Field(type = FieldType.Keyword)
    private String documentTitle;

    @Field(type = FieldType.Integer)
    private Integer position;
}
