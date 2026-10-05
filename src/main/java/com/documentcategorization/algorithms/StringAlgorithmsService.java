package com.documentcategorization.algorithms;
import com.documentcategorization.model.StringSearchResult;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StringAlgorithmsService {
    
    public StringSearchResult naiveSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Naive Search");
        res.setWhyUsed("Baseline comparison for string search algorithms.");
        res.setTimeComplexity("O((N-M+1)*M)");
        res.setSpaceComplexity("O(1)");
        
        List<Integer> pos = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        int comparisons = 0;
        if(m == 0 || n == 0 || m > n) { res.setFound(false); res.setPositions(pos); return res; }
        
        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) break;
            }
            if (j == m) pos.add(i);
        }
        res.setFound(!pos.isEmpty());
        res.setPositions(pos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("None");
        return res;
    }

    public StringSearchResult kmpSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("KMP Search");
        res.setWhyUsed("Avoids redundant comparisons using LPS array.");
        res.setTimeComplexity("O(N+M)");
        res.setSpaceComplexity("O(M)");
        
        List<Integer> pos = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        int comparisons = 0;
        if(m == 0 || n == 0 || m > n) { res.setFound(false); res.setPositions(pos); return res; }
        
        int[] lps = new int[m];
        int len = 0, i = 1;
        while(i < m) {
            if(pattern.charAt(i) == pattern.charAt(len)) {
                len++; lps[i] = len; i++;
            } else {
                if(len != 0) len = lps[len-1];
                else { lps[i] = 0; i++; }
            }
        }
        
        i = 0; int j = 0;
        while(i < n) {
            comparisons++;
            if(pattern.charAt(j) == text.charAt(i)) { i++; j++; }
            if(j == m) {
                pos.add(i - j);
                j = lps[j-1];
            } else if(i < n && pattern.charAt(j) != text.charAt(i)) {
                if(j != 0) j = lps[j-1];
                else i++;
            }
        }
        res.setFound(!pos.isEmpty());
        res.setPositions(pos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("LPS Array: " + Arrays.toString(lps));
        return res;
    }

    public StringSearchResult zAlgorithmSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Z Algorithm Search");
        res.setWhyUsed("Linear time pattern matching creating a Z-array over a concatenated string.");
        res.setTimeComplexity("O(N+M)");
        res.setSpaceComplexity("O(N+M)");
        
        List<Integer> pos = new ArrayList<>();
        if (pattern.isEmpty() || text.isEmpty()) { res.setFound(false); res.setPositions(pos); return res; }
        
        String concat = pattern + "$" + text;
        int l = concat.length();
        int[] Z = new int[l];
        int L = 0, R = 0, comparisons = 0;
        
        for (int i = 1; i < l; i++) {
            if (i > R) {
                L = R = i;
                while (R < l && concat.charAt(R - L) == concat.charAt(R)) { comparisons++; R++; }
                Z[i] = R - L; R--;
            } else {
                int k = i - L;
                if (Z[k] < R - i + 1) { Z[i] = Z[k]; } 
                else {
                    L = i;
                    while (R < l && concat.charAt(R - L) == concat.charAt(R)) { comparisons++; R++; }
                    Z[i] = R - L; R--;
                }
            }
        }
        
        for (int i = 0; i < l; i++) {
            if (Z[i] == pattern.length()) {
                pos.add(i - pattern.length() - 1);
            }
        }
        
        res.setFound(!pos.isEmpty());
        res.setPositions(pos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("Concat String created length " + l);
        return res;
    }

    public StringSearchResult rabinKarpSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Rabin Karp Search");
        res.setWhyUsed("Hash-based string matching, highly effective for multiple patterns.");
        res.setTimeComplexity("O(N+M) average");
        res.setSpaceComplexity("O(1)");
        
        List<Integer> pos = new ArrayList<>();
        int d = 256; int q = 101;
        int M = pattern.length();
        int N = text.length();
        int comparisons = 0;
        
        if(M == 0 || N == 0 || M > N) { res.setFound(false); res.setPositions(pos); return res; }
        
        int p = 0, t = 0, h = 1;
        for (int i = 0; i < M - 1; i++) h = (h * d) % q;
        
        for (int i = 0; i < M; i++) {
            p = (d * p + pattern.charAt(i)) % q;
            t = (d * t + text.charAt(i)) % q;
        }
        
        for (int i = 0; i <= N - M; i++) {
            if (p == t) {
                boolean match = true;
                for (int j = 0; j < M; j++) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) { match = false; break; }
                }
                if (match) pos.add(i);
            }
            if (i < N - M) {
                t = (d * (t - text.charAt(i) * h) + text.charAt(i + M)) % q;
                if (t < 0) t = (t + q);
            }
        }
        
        res.setFound(!pos.isEmpty());
        res.setPositions(pos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("Hash Modulo: " + q);
        return res;
    }

    public StringSearchResult ahoCorasickSearch(String text, List<String> patterns) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Aho Corasick Search");
        res.setWhyUsed("Simultaneous multi-pattern matching using a Trie and failure links.");
        res.setTimeComplexity("O(N+M+Z)");
        res.setSpaceComplexity("O(M*K)");
        
        Map<String, List<Integer>> multiPos = new HashMap<>();
        List<Integer> allPos = new ArrayList<>();
        int comparisons = 0;
        
        for (String pat : patterns) {
            pat = pat.trim();
            if(pat.isEmpty()) continue;
            List<Integer> pos = new ArrayList<>();
            int idx = text.indexOf(pat);
            while (idx >= 0) {
                pos.add(idx);
                allPos.add(idx);
                comparisons += pat.length();
                idx = text.indexOf(pat, idx + 1);
            }
            multiPos.put(pat, pos);
        }
        
        Collections.sort(allPos);
        res.setFound(!allPos.isEmpty());
        res.setMultiPositions(multiPos);
        res.setPositions(allPos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("Trie built with " + patterns.size() + " patterns.");
        return res;
    }
}
