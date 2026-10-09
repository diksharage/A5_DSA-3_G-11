package com.documentcategorization.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;

@Service
public class TextExtractionService {
    
    private final Tika tika;
    
    public TextExtractionService() {
        this.tika = new Tika();
        // Increase max string length for large PDFs if needed, default is 100k
        tika.setMaxStringLength(-1); 
    }
    
    public String extractText(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (filename.endsWith(".txt")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } else {
            // Use Tika for PDF and DOCX
            String text = tika.parseToString(file.getInputStream());
            if (text != null) {
                return text.trim();
            }
            return "";
        }
    }
}
