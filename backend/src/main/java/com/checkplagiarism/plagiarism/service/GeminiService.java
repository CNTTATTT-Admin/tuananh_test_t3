package com.checkplagiarism.plagiarism.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeminiService {

    @Value("${tuna.gemini.api-key:}")
    private String apiKey;

    @Value("${tuna.gemini.model:gemini-1.5-flash}")
    private String model;

    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Tạo bản tóm tắt phân tích đạo văn từ AI.
     */
    public String generateSummary(String inputText, List<String> matchedBlocks) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Gemini API key is not configured. AI Summary skipped.");
            return "Khóa API Gemini chưa được cấu hình. Không thể tạo bản tóm tắt từ AI.";
        }

        // Giới hạn độ dài văn bản đầu vào để tránh vượt quá token limit
        String truncatedInput = inputText.length() > 5000 ? inputText.substring(0, 5000) + "..." : inputText;

        String prompt = "Dưới đây là một bài nộp của sinh viên và danh sách các đoạn được cho là trùng khớp với các tài liệu khác thông qua so khớp vân tay (fingerprinting). " +
                "Hãy đóng vai giảng viên và đưa ra một bản tóm tắt chuyên nghiệp bằng tiếng Việt (khoảng 200-300 từ) về bản báo cáo này: " +
                "\n1. Nhận định về mức độ sao chép tổng thể." +
                "\n2. Phân biệt xem đây là sao chép trực tiếp hay là 'xào nấu' (paraphrasing) tinh vi." +
                "\n3. Đưa ra lời khuyên cho sinh viên về cách trích dẫn nếu có." +
                "\n\nNội dung bài nộp: \n\"" + truncatedInput + "\"" +
                "\n\nCác đoạn trùng khớp được tìm thấy: \n" + String.join("\n - ", matchedBlocks) +
                "\n\nHãy viết một bản tóm tắt mạch lạc, khách quan.";

        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        try {
            String url = GEMINI_BASE_URL + model + ":generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", Collections.singletonList(content));

            // Cấu hình an toàn đơn giản - không bắt buộc nhưng tốt cho Gemini
            Map<String, Object> safety = new HashMap<>();
            safety.put("category", "HARM_CATEGORY_HARASSMENT");
            safety.put("threshold", "BLOCK_NONE");
            body.put("safetySettings", Collections.singletonList(safety));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentRes = (Map<String, Object>) candidate.get("content");
                    if (contentRes != null && contentRes.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentRes.get("parts");
                        if (!parts.isEmpty()) {
                            return (String) parts.get(0).get("text");
                        }
                    }
                }
            }
            return "Không thể trích xuất nội dung từ phản hồi của Gemini.";
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            return "Lỗi khi gọi AI phân tích: " + e.getMessage();
        }
    }
}
