package com.checkplagiarism.plagiarism.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.FingerPrint;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FingerprintService {
private String normalizeText(String text) {
    return text
        .toLowerCase()
        .replaceAll("\\s+", " ")
        .trim();
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
        int k = 5;
        List<String> words = Arrays.asList(text.split("\\s+"));

        List<FingerPrint> list = new ArrayList<>();

        for (int i = 0; i <= words.size() - k; i++) {

            String gram = String.join(" ", words.subList(i, i + k));

            long hash = hash(gram);

            list.add(new FingerPrint(hash, i));
        }

        return list;
    }
}
