package com.checkplagiarism.plagiarism.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultPaginationDTO {
    private Meta meta;
    private Object results;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  Meta {
        private int page;
        private int pageSize;
        private long total;
        private int pages;
    }
}
