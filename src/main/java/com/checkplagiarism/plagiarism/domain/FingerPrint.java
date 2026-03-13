package com.checkplagiarism.plagiarism.domain;

public class FingerPrint {
    private long hash;
    private int position;

    public FingerPrint(long hash, int position) {
        this.hash = hash;
        this.position = position;
    }

    public long getHash() {
        return hash;
    }

    public int getPosition() {
        return position;
    }
}
