package com.checkplagiarism.plagiarism.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.MurmurHash3;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FingerprintService {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "bị", "bởi", "cả", "cái", "càng", "chiếc", "cho", "chứ", "chưa", "chuyện", "có", "cũng", "cuối", "cuộc", "cửa", 
        "dùng", "dưới", "đã", "đang", "đi", "đến", "đây", "đó", "được", "đưa", "giữa", "hay", "hãy", "hơn", "khi", 
        "không", "là", "lại", "làm", "lên", "lúc", "mà", "mỗi", "một", "này", "nên", "nếu", "ngay", "nhiều", "như", 
        "nhưng", "những", "nơi", "nữa", "phải", "qua", "quá", "ra", "rằng", "rất", "rồi", "sau", "sẽ", "so", "sự", 
        "tại", "theo", "thế", "thì", "thôi", "trong", "trên", "trước", "từ", "tới", "và", "vẫn", "về", "việc", "với", "vừa"
    ));

    public String normalizeText(String text) {
        if (text == null) return "";
        
        text = text.toLowerCase();
        // Remove most special characters but keep alphanumeric and Vietnamese accents
        text = text.replaceAll("[^a-z0-9àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ\\s]", " ");
        
        // Remove stop words and collapse whitespaces
        return Arrays.stream(text.split("\\s+"))
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.joining(" "));
    }

    public List<String> generateNGrams(String text, int n) {
        String[] words = text.split("\\s+");
        List<String> grams = new ArrayList<>();

        if (words.length < n && words.length > 0) {
            // Fallback for short text: use all words as one gram
            grams.add(String.join(" ", words));
            return grams;
        }

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder gram = new StringBuilder();
            for (int j = 0; j < n; j++) {
                gram.append(words[i + j]).append(j < n - 1 ? " " : "");
            }
            grams.add(gram.toString());
        }
        return grams;
    }

    public long hash(String gram) {
        // Using MurmurHash3_x86_32 for better distribution and speed
        byte[] bytes = gram.getBytes();
        // Return absolute value to avoid negative issues in some DB/ES setups if needed
        // but Murmur3 can be negative. Let's just use it consistently.
        return (long) MurmurHash3.hash32x86(bytes, 0, bytes.length, 42); 
    }

    public Set<Long> generateFingerprints(String text) {
        return generateFingerprintsWithPositions(text).keySet();
    }

    public Map<Long, List<Integer>> generateFingerprintsWithPositions(String text) {
        text = normalizeText(text);
        List<String> grams = generateNGrams(text, 3); 

        Map<Long, List<Integer>> hashToPositions = new HashMap<>();
        for (int i = 0; i < grams.size(); i++) {
            long h = hash(grams.get(i));
            hashToPositions.computeIfAbsent(h, k -> new ArrayList<>()).add(i);
        }
        return hashToPositions;
    }
}
