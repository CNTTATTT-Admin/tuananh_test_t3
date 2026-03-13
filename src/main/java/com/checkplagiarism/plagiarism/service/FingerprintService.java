package com.checkplagiarism.plagiarism.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.FingerPrint;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FingerprintService {
     public String normalizeText(String text) {

        text = text.toLowerCase();

        text = text.replaceAll("[^a-z0-9 ]", " ");

        text = text.replaceAll("\\s+", " ");

        return text;
    }

    public List<String> generateNGrams(String text, int n) {

        String[] words = text.split(" ");

        List<String> grams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < n; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }

    public long hash(String gram) {

        return gram.hashCode();
    }

    public List<FingerPrint> generateFingerprints(String text) {

        text = normalizeText(text);

        List<String> grams = generateNGrams(text, 5);

        List<FingerPrint> fingerprints = new ArrayList<>();

        int pos = 0;

        for (String gram : grams) {

            long hash = hash(gram);

            fingerprints.add(
                    new FingerPrint(hash, pos++));
        }

        return fingerprints;
    }
}
