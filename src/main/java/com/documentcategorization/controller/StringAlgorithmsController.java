package com.documentcategorization.controller;
import com.documentcategorization.algorithms.StringAlgorithmsService;
import com.documentcategorization.model.StringSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
@Controller
public class StringAlgorithmsController {
    @Autowired private StringAlgorithmsService stringAlgorithmsService;
    @GetMapping("/string-algorithms")
    public String index(Model model) { return "string-algorithms"; }
    @PostMapping("/string-algorithms/run")
    public String run(@RequestParam("text") String text, @RequestParam("pattern") String pattern, @RequestParam("algorithm") String algorithm, Model model) {
        StringSearchResult result = null;
        if(algorithm.equals("naive")) result = stringAlgorithmsService.naiveSearch(text, pattern);
        else if(algorithm.equals("kmp")) result = stringAlgorithmsService.kmpSearch(text, pattern);
        else if(algorithm.equals("z")) result = stringAlgorithmsService.zAlgorithmSearch(text, pattern);
        else if(algorithm.equals("rabin")) result = stringAlgorithmsService.rabinKarpSearch(text, pattern);
        else if(algorithm.equals("ahocorasick")) result = stringAlgorithmsService.ahoCorasickSearch(text, Arrays.asList(pattern.split(",")));
        
        model.addAttribute("result", result);
        model.addAttribute("inputText", text);
        model.addAttribute("inputPattern", pattern);
        model.addAttribute("selectedAlgorithm", algorithm);
        return "string-algorithms";
    }
}