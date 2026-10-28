package com.documentcategorization.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    public Object handleMultipartError(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String msg = "Upload error: File size exceeds the maximum allowed limit or multipart request is invalid.";
        if (request != null && request.getRequestURI() != null && request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("error", msg);
            return ResponseEntity.badRequest().body(body);
        }
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/";
    }

    @ExceptionHandler(Throwable.class)
    public Object handleUnexpectedError(Throwable ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String detail = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        if (request != null && request.getRequestURI() != null && request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("error", "Request could not be completed: " + detail);
            return ResponseEntity.badRequest().body(body);
        }
        redirectAttributes.addFlashAttribute("message", "Action could not be completed: " + detail);
        return "redirect:/";
    }
}
