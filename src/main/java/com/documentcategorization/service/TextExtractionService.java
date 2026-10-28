package com.documentcategorization.service;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class TextExtractionService {

    private final Tika tika;

    public TextExtractionService() {
        this.tika = new Tika();
        tika.setMaxStringLength(-1);
    }

    public String extractText(MultipartFile file) throws Exception {
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        String filename = originalName.toLowerCase();
        byte[] bytes = file.getBytes();

        if (bytes == null || bytes.length == 0) {
            return "";
        }

        if (filename.endsWith(".txt")) {
            String raw = new String(bytes, StandardCharsets.UTF_8);
            return raw.replace("\uFEFF", "").trim();
        } else if (filename.endsWith(".pdf")) {
            // Verify PDF header (%PDF-)
            if (bytes.length < 5 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F' || bytes[4] != '-') {
                throw new IllegalArgumentException("Invalid or corrupted PDF file: " + originalName);
            }
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, originalName);
            String text = tika.parseToString(new ByteArrayInputStream(bytes), metadata);
            return text != null ? text.replace("\uFEFF", "").trim() : "";
        } else if (filename.endsWith(".docx")) {
            // Verify ZIP/OOXML header (PK\x03\x04)
            if (bytes.length < 4 || bytes[0] != 'P' || bytes[1] != 'K' || bytes[2] != 3 || bytes[3] != 4) {
                throw new IllegalArgumentException("Invalid or corrupted DOCX file: " + originalName);
            }
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, originalName);
            String text = tika.parseToString(new ByteArrayInputStream(bytes), metadata);
            return text != null ? text.replace("\uFEFF", "").trim() : "";
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + originalName);
        }
    }
}
