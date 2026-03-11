package com.checkplagiarism.plagiarism.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    private final String UPLOAD_DIR = "uploads/";
    
    public String extractText(MultipartFile file) throws IOException{
        String fileName= file.getOriginalFilename();
        if (fileName.endsWith(".pdf")) {
            return extractTextFromPDF(file);
        }
        if (fileName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        }
        if (fileName.endsWith(".txt")) {
            return new String(file.getBytes());
        }
        throw new RuntimeException("unsuported file");
    }

        public String extractTextFromPDF(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);

        document.close();
        return text;
    }

    public String extractTextFromDocx(MultipartFile file) throws IOException {
    XWPFDocument document = new XWPFDocument(file.getInputStream());
    StringBuilder text = new StringBuilder();
    document.getParagraphs().forEach(p -> {
        text.append(p.getText()).append("\n");
    });
    document.close();

    return text.toString();
}
    public String saveFile(MultipartFile file) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path path = Paths.get(UPLOAD_DIR + fileName);

        Files.createDirectories(path.getParent());

        Files.write(path, file.getBytes());

        return path.toString();
    }
}
