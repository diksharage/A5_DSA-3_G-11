package com.documentcategorization.algorithms;

import com.documentcategorization.model.StringSearchResult;
import com.documentcategorization.model.WalkthroughStep;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StringAlgorithmsService {

    private static final int MAX_DETAILED_STEPS = 350;

    public StringSearchResult naiveSearch(String text, String pattern) {
        return naiveSearch(text, pattern, null);
    }

    public StringSearchResult naiveSearch(String text, String pattern, List<WalkthroughStep> steps) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Naive Search");
        res.setWhyUsed("Baseline comparison for string search algorithms.");
        res.setTimeComplexity("O((N-M+1)*M)");
        res.setSpaceComplexity("O(1)");

        List<Integer> pos = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        int comparisons = 0;

        if (steps != null) {
            Map<String, Object> init = new LinkedHashMap<>();
            init.put("Text Length (N)", n);
            init.put("Pattern (M = " + m + ")", pattern);
            init.put("Total Alignments to Check", (m > 0 && n >= m) ? (n - m + 1) : 0);
            init.put("Text Preview", previewText(text, 0, m));
            addStep(steps, "1. Initialization", "Initialize Naive String Matching",
                    "Sliding pattern '" + pattern + "' (length M = " + m + ") across text of length N = " + n + " from alignment i = 0 to " + Math.max(0, n - m) + ".",
                    init);
        }

        if (m == 0 || n == 0 || m > n) {
            res.setFound(false);
            res.setPositions(pos);
            if (steps != null) {
                Map<String, Object> end = new LinkedHashMap<>();
                end.put("Final Matching Positions", pos);
                end.put("Total Character Comparisons", comparisons);
                addStep(steps, "Final Result", "Search Complete (No Valid Alignments)",
                        "Pattern length M = " + m + ", Text length N = " + n + ". No matches possible.", end);
            }
            return res;
        }

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                comparisons++;
                char tc = text.charAt(i + j);
                char pc = pattern.charAt(j);
                boolean charMatch = (tc == pc);

                if (steps != null && canAddDetailedStep(steps)) {
                    Map<String, Object> state = new LinkedHashMap<>();
                    state.put("Current Alignment (i)", i);
                    state.put("Pattern Offset (j)", j);
                    state.put("Current Text Position (i + j)", i + j);
                    state.put("Text Window at Alignment i", extractWindow(text, i, m));
                    state.put("Characters Compared", "text[" + (i + j) + "] = '" + formatChar(tc) + "' vs pattern[" + j + "] = '" + formatChar(pc) + "'");
                    state.put("Comparison Result", charMatch ? "MATCH" : "MISMATCH");
                    state.put("Comparisons So Far", comparisons);
                    state.put("Matches Found So Far", new ArrayList<>(pos));
                    addStep(steps, "2. Character Comparison",
                            "Alignment i = " + i + ", Compare text[" + (i + j) + "] ('" + formatChar(tc) + "') vs pattern[" + j + "] ('" + formatChar(pc) + "')",
                            charMatch
                                    ? "Characters match ('" + formatChar(tc) + "' == '" + formatChar(pc) + "'). Advance pattern index j to " + (j + 1) + "."
                                    : "Mismatch ('" + formatChar(tc) + "' != '" + formatChar(pc) + "'). Break inner loop and shift alignment to i = " + (i + 1) + ".",
                            state);
                }

                if (!charMatch) break;
            }
            if (j == m) {
                pos.add(i);
                if (steps != null) {
                    Map<String, Object> matchState = new LinkedHashMap<>();
                    matchState.put("Matched Alignment Position (i)", i);
                    matchState.put("Matched Substring", text.substring(i, i + m));
                    matchState.put("Comparisons So Far", comparisons);
                    matchState.put("Matching Positions Discovered", new ArrayList<>(pos));
                    addStep(steps, "3. Pattern Match Found",
                            "Full Pattern Match at Index " + i,
                            "All M = " + m + " characters of pattern '" + pattern + "' matched starting at text index " + i + ".",
                            matchState);
                }
            }
        }

        res.setFound(!pos.isEmpty());
        res.setPositions(pos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("None");

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Pattern", pattern);
            finalState.put("Total Character Comparisons", comparisons);
            finalState.put("Total Matches Found", pos.size());
            finalState.put("Final Matching Positions", new ArrayList<>(pos));
            addStep(steps, "4. Final Result",
                    "Naive Search Complete — " + pos.size() + " Match(es) Found",
                    "Completed Naive String Matching after " + comparisons + " character comparison(s). Final matching positions: " + pos + ".",
                    finalState);
        }
        return res;
    }

    public StringSearchResult kmpSearch(String text, String pattern) {
        return kmpSearch(text, pattern, null);
    }

    public StringSearchResult kmpSearch(String text, String pattern, List<WalkthroughStep> steps) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("KMP Search");
        res.setWhyUsed("Avoids redundant comparisons using LPS array.");
        res.setTimeComplexity("O(N+M)");
        res.setSpaceComplexity("O(M)");

        List<Integer> pos = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        int comparisons = 0;
        if (m == 0 || n == 0 || m > n) {
            res.setFound(false);
            res.setPositions(pos);
            return res;
        }

        int[] lps = new int[m];
        int len = 0, i = 1;

        if (steps != null) {
            Map<String, Object> initState = new LinkedHashMap<>();
            initState.put("Pattern", pattern);
            initState.put("Pattern Length (M)", m);
            initState.put("Text Length (N)", n);
            initState.put("Initial LPS Array", Arrays.toString(lps));
            addStep(steps, "1. LPS Table Initialization",
                    "Initialize LPS Table of Size M = " + m,
                    "Starting KMP preprocessing to build Longest Proper Prefix which is also Suffix (LPS) array for pattern '" + pattern + "'. lps[0] is always 0.",
                    initState);
        }

        while (i < m) {
            char ci = pattern.charAt(i);
            char clen = pattern.charAt(len);
            if (ci == clen) {
                len++;
                lps[i] = len;
                if (steps != null && canAddDetailedStep(steps)) {
                    Map<String, Object> lpsState = new LinkedHashMap<>();
                    lpsState.put("Pattern Index (i)", i);
                    lpsState.put("Prefix Length Pointer (len)", len);
                    lpsState.put("Comparison", "pattern[" + i + "] ('" + formatChar(ci) + "') == pattern[" + (len - 1) + "] ('" + formatChar(clen) + "')");
                    lpsState.put("Updated lps[" + i + "]", len);
                    lpsState.put("Current LPS Array", Arrays.toString(lps));
                    addStep(steps, "1. LPS Table Construction",
                            "LPS Match at i = " + i + " -> lps[" + i + "] = " + len,
                            "pattern[" + i + "] matches pattern[" + (len - 1) + "] ('" + formatChar(ci) + "'). Incremented prefix length to " + len + " and set lps[" + i + "] = " + len + ".",
                            lpsState);
                }
                i++;
            } else {
                if (len != 0) {
                    int oldLen = len;
                    len = lps[len - 1];
                    if (steps != null && canAddDetailedStep(steps)) {
                        Map<String, Object> lpsState = new LinkedHashMap<>();
                        lpsState.put("Pattern Index (i)", i);
                        lpsState.put("Mismatch", "pattern[" + i + "] ('" + formatChar(ci) + "') != pattern[" + oldLen + "] ('" + formatChar(clen) + "')");
                        lpsState.put("Fallback len = lps[" + (oldLen - 1) + "]", len);
                        lpsState.put("Current LPS Array", Arrays.toString(lps));
                        addStep(steps, "1. LPS Table Construction",
                                "LPS Mismatch at i = " + i + " -> Fallback len to " + len,
                                "pattern[" + i + "] != pattern[" + oldLen + "]. Falling back len from " + oldLen + " to lps[" + (oldLen - 1) + "] = " + len + " without incrementing i.",
                                lpsState);
                    }
                } else {
                    lps[i] = 0;
                    if (steps != null && canAddDetailedStep(steps)) {
                        Map<String, Object> lpsState = new LinkedHashMap<>();
                        lpsState.put("Pattern Index (i)", i);
                        lpsState.put("Mismatch", "pattern[" + i + "] ('" + formatChar(ci) + "') != pattern[0] ('" + formatChar(clen) + "')");
                        lpsState.put("Updated lps[" + i + "]", 0);
                        lpsState.put("Current LPS Array", Arrays.toString(lps));
                        addStep(steps, "1. LPS Table Construction",
                                "LPS Mismatch at i = " + i + " with len = 0 -> lps[" + i + "] = 0",
                                "pattern[" + i + "] != pattern[0] and len == 0. Set lps[" + i + "] = 0 and advance i to " + (i + 1) + ".",
                                lpsState);
                    }
                    i++;
                }
            }
        }

        i = 0;
        int j = 0;
        while (i < n) {
            comparisons++;
            int curI = i;
            int curJ = j;
            char tc = text.charAt(curI);
            char pc = pattern.charAt(curJ);
            boolean matchedChar = (pc == tc);

            if (matchedChar) {
                i++;
                j++;
            }

            if (steps != null && canAddDetailedStep(steps)) {
                Map<String, Object> cmpState = new LinkedHashMap<>();
                cmpState.put("Text Position (i)", curI);
                cmpState.put("Pattern Position (j)", curJ);
                cmpState.put("Current Alignment (i - j)", curI - curJ);
                cmpState.put("Text Window", extractWindow(text, Math.max(0, curI - curJ), m));
                cmpState.put("Characters Compared", "text[" + curI + "] = '" + formatChar(tc) + "' vs pattern[" + curJ + "] = '" + formatChar(pc) + "'");
                cmpState.put("Comparison Result", matchedChar ? "MATCH (advance i -> " + i + ", j -> " + j + ")" : "MISMATCH");
                cmpState.put("LPS Array", Arrays.toString(lps));
                cmpState.put("Comparisons So Far", comparisons);
                cmpState.put("Matches Found So Far", new ArrayList<>(pos));
                addStep(steps, "2. KMP Character Comparison",
                        "Compare text[" + curI + "] ('" + formatChar(tc) + "') vs pattern[" + curJ + "] ('" + formatChar(pc) + "')",
                        matchedChar
                                ? "Characters match ('" + formatChar(tc) + "'). Advanced text index i to " + i + " and pattern index j to " + j + "."
                                : "Mismatch between text[" + curI + "] ('" + formatChar(tc) + "') and pattern[" + curJ + "] ('" + formatChar(pc) + "').",
                        cmpState);
            }

            if (j == m) {
                int matchIdx = i - j;
                pos.add(matchIdx);
                int nextJ = lps[j - 1];
                if (steps != null) {
                    Map<String, Object> matchState = new LinkedHashMap<>();
                    matchState.put("Match Found at Text Index (i - j)", matchIdx);
                    matchState.put("Matched Substring", text.substring(matchIdx, matchIdx + m));
                    matchState.put("LPS Shift (j = lps[" + (j - 1) + "])", nextJ);
                    matchState.put("LPS Array", Arrays.toString(lps));
                    matchState.put("Matching Positions Discovered", new ArrayList<>(pos));
                    addStep(steps, "3. Pattern Match & LPS Shift",
                            "Full Match at Index " + matchIdx + " -> Shift j to lps[" + (j - 1) + "] = " + nextJ,
                            "Reached j == M (" + m + "). Recorded match at index " + matchIdx + " and shifted j to lps[" + (j - 1) + "] = " + nextJ + " without backtracking i.",
                            matchState);
                }
                j = nextJ;
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    int oldJ = j;
                    j = lps[j - 1];
                    if (steps != null && canAddDetailedStep(steps)) {
                        Map<String, Object> shiftState = new LinkedHashMap<>();
                        shiftState.put("Text Position (i)", i);
                        shiftState.put("Previous Pattern Position (j)", oldJ);
                        shiftState.put("New Pattern Position (j = lps[" + (oldJ - 1) + "])", j);
                        shiftState.put("New Alignment (i - j)", i - j);
                        shiftState.put("LPS Array", Arrays.toString(lps));
                        addStep(steps, "4. KMP Pattern Shift via LPS",
                                "Mismatch at text[" + i + "] -> Shift Pattern j from " + oldJ + " to lps[" + (oldJ - 1) + "] = " + j,
                                "Using LPS table, shifted pattern index j from " + oldJ + " to lps[" + (oldJ - 1) + "] = " + j + " while keeping text index i = " + i + ".",
                                shiftState);
                    }
                } else {
                    int oldI = i;
                    i++;
                    if (steps != null && canAddDetailedStep(steps)) {
                        Map<String, Object> shiftState = new LinkedHashMap<>();
                        shiftState.put("Previous Text Position (i)", oldI);
                        shiftState.put("New Text Position (i)", i);
                        shiftState.put("Pattern Position (j)", 0);
                        shiftState.put("LPS Array", Arrays.toString(lps));
                        addStep(steps, "4. Advance Text Pointer",
                                "Mismatch at j = 0 -> Advance Text Index i from " + oldI + " to " + i,
                                "Mismatch at pattern start (j == 0). Incremented text index i from " + oldI + " to " + i + ".",
                                shiftState);
                    }
                }
            }
        }

        res.setFound(!pos.isEmpty());
        res.setPositions(pos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("LPS Array computed in O(M)");

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Pattern", pattern);
            finalState.put("Computed LPS Array", Arrays.toString(lps));
            finalState.put("Total Comparisons", comparisons);
            finalState.put("Final Matching Positions", new ArrayList<>(pos));
            addStep(steps, "5. Final Result",
                    "KMP Search Complete — " + pos.size() + " Match(es) Found",
                    "Finished KMP search in " + comparisons + " comparison(s). Final matching positions: " + pos + ".",
                    finalState);
        }
        return res;
    }

    public StringSearchResult zAlgorithmSearch(String text, String pattern) {
        return zAlgorithmSearch(text, pattern, null);
    }

    public StringSearchResult zAlgorithmSearch(String text, String pattern, List<WalkthroughStep> steps) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Z Algorithm Search");
        res.setWhyUsed("Linear time pattern matching creating a Z-array over a concatenated string.");
        res.setTimeComplexity("O(N+M)");
        res.setSpaceComplexity("O(N+M)");

        List<Integer> pos = new ArrayList<>();
        if (pattern.isEmpty() || text.isEmpty()) {
            res.setFound(false);
            res.setPositions(pos);
            return res;
        }

        String concat = pattern + "$" + text;
        int l = concat.length();
        int m = pattern.length();
        int[] Z = new int[l];
        int L = 0, R = 0, comparisons = 0;

        if (steps != null) {
            Map<String, Object> initState = new LinkedHashMap<>();
            initState.put("Pattern (M = " + m + ")", pattern);
            initState.put("Text Length (N)", text.length());
            initState.put("Combined String (P + '$' + T)", previewText(concat, 0, Math.min(l, 60)));
            initState.put("Combined Length (M + 1 + N)", l);
            initState.put("Initial Z-Box [L, R]", "[0, 0]");
            addStep(steps, "1. Combined String Construction",
                    "Construct Concatenated Sequence: pattern + '$' + text",
                    "Created combined string of length " + l + " by joining pattern '" + pattern + "', delimiter '$', and the target text. Any index i where Z[i] == " + m + " indicates a pattern match at text index (i - " + (m + 1) + ").",
                    initState);
        }

        for (int i = 1; i < l; i++) {
            String caseDesc;
            if (i > R) {
                L = R = i;
                while (R < l && concat.charAt(R - L) == concat.charAt(R)) {
                    comparisons++;
                    R++;
                }
                Z[i] = R - L;
                R--;
                caseDesc = "i > R (outside Z-box): explicitly matched prefix from i = " + i + ", setting Z[" + i + "] = " + Z[i] + " and Z-box [L, R] = [" + L + ", " + R + "]";
            } else {
                int k = i - L;
                if (Z[k] < R - i + 1) {
                    Z[i] = Z[k];
                    caseDesc = "i <= R (inside Z-box) and Z[k=" + k + "] (" + Z[k] + ") < R - i + 1 (" + (R - i + 1) + "): copied Z[" + i + "] = Z[" + k + "] = " + Z[i] + " without character comparisons";
                } else {
                    L = i;
                    while (R < l && concat.charAt(R - L) == concat.charAt(R)) {
                        comparisons++;
                        R++;
                    }
                    Z[i] = R - L;
                    R--;
                    caseDesc = "i <= R (inside Z-box) and Z[k=" + k + "] >= R - i + 1: extended Z-box from R, setting Z[" + i + "] = " + Z[i] + " and new Z-box [L, R] = [" + L + ", " + R + "]";
                }
            }

            boolean isFullMatch = (Z[i] == m);
            if (steps != null && (canAddDetailedStep(steps) || isFullMatch)) {
                Map<String, Object> zState = new LinkedHashMap<>();
                zState.put("Current Z-Array Index (i)", i);
                zState.put("Character concat[i]", "'" + formatChar(concat.charAt(i)) + "'");
                if (i > m) {
                    zState.put("Corresponding Text Index (i - M - 1)", i - m - 1);
                }
                zState.put("Computed Z[" + i + "] Value", Z[i]);
                zState.put("Current Z-Box Boundaries [L, R]", "[" + L + ", " + R + "]");
                zState.put("Z-Array Window", formatZSlice(Z, i));
                zState.put("Comparisons So Far", comparisons);
                if (isFullMatch) {
                    zState.put("Match Detected", "Z[" + i + "] == M (" + m + ") -> Match at Text Index " + (i - m - 1));
                }
                addStep(steps, isFullMatch ? "3. Z-Value Match Detected (Z[i] == M)" : "2. Z-Array Computation",
                        "Compute Z[" + i + "] = " + Z[i] + (isFullMatch ? " (MATCH at Text Index " + (i - m - 1) + ")" : ""),
                        caseDesc,
                        zState);
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
        res.setPreprocessingInfo("Z-Array constructed over concat string.");

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Pattern", pattern);
            finalState.put("Pattern Length (M)", m);
            finalState.put("Total Prefix Comparisons", comparisons);
            finalState.put("Z-Array Summary", formatZSlice(Z, Math.min(l - 1, 30)));
            finalState.put("Final Matching Positions", new ArrayList<>(pos));
            addStep(steps, "4. Final Result",
                    "Z Algorithm Complete — " + pos.size() + " Match(es) Found",
                    "Scanned Z-array for entries where Z[i] == " + m + " and mapped each to text position (i - " + m + " - 1). Final matching positions: " + pos + ".",
                    finalState);
        }
        return res;
    }

    public StringSearchResult rabinKarpSearch(String text, String pattern) {
        return rabinKarpSearch(text, pattern, null);
    }

    public StringSearchResult rabinKarpSearch(String text, String pattern, List<WalkthroughStep> steps) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Rabin Karp Search");
        res.setWhyUsed("Hash-based string matching, highly effective for multiple patterns.");
        res.setTimeComplexity("O(N+M) average");
        res.setSpaceComplexity("O(1)");

        List<Integer> pos = new ArrayList<>();
        int d = 256;
        int q = 101;
        int M = pattern.length();
        int N = text.length();
        int comparisons = 0;

        if (M == 0 || N == 0 || M > N) {
            res.setFound(false);
            res.setPositions(pos);
            return res;
        }

        int p = 0, t = 0, h = 1;
        for (int i = 0; i < M - 1; i++) h = (h * d) % q;

        for (int i = 0; i < M; i++) {
            p = (d * p + pattern.charAt(i)) % q;
            t = (d * t + text.charAt(i)) % q;
        }

        if (steps != null) {
            Map<String, Object> initState = new LinkedHashMap<>();
            initState.put("Pattern (M = " + M + ")", pattern);
            initState.put("Radix Base (d)", d);
            initState.put("Prime Modulus (q)", q);
            initState.put("Leading Digit Multiplier (h = d^(M-1) mod q)", h);
            initState.put("Pattern Hash (p)", p);
            initState.put("Initial Window text[0.." + (M - 1) + "]", text.substring(0, M));
            initState.put("Initial Window Hash (t)", t);
            addStep(steps, "1. Initial Hash Computation",
                    "Compute Pattern Hash p = " + p + " and Initial Window Hash t = " + t,
                    "Computed polynomial rolling hash (d = 256, q = 101, h = " + h + ") for pattern '" + pattern + "' (p = " + p + ") and initial text window '" + text.substring(0, M) + "' (t = " + t + ").",
                    initState);
        }

        for (int i = 0; i <= N - M; i++) {
            int currentWindowHash = t;
            String windowStr = text.substring(i, i + M);
            boolean hashEqual = (p == t);

            if (!hashEqual && steps != null && canAddDetailedStep(steps)) {
                Map<String, Object> cmpState = new LinkedHashMap<>();
                cmpState.put("Window Start Position (i)", i);
                cmpState.put("Current Window Substring", windowStr);
                cmpState.put("Pattern Hash (p)", p);
                cmpState.put("Current Window Hash (t)", currentWindowHash);
                cmpState.put("Hash Comparison", "t (" + currentWindowHash + ") != p (" + p + ") -> Skip character verification");
                cmpState.put("Matches Found So Far", new ArrayList<>(pos));
                addStep(steps, "2. Hash Comparison",
                        "Window i = " + i + " ('" + windowStr + "'): Hash t = " + currentWindowHash + " != p = " + p,
                        "Window hash (" + currentWindowHash + ") does not equal pattern hash (" + p + "). No character-by-character verification needed.",
                        cmpState);
            }

            if (p == t) {
                boolean match = true;
                int charsChecked = 0;
                for (int j = 0; j < M; j++) {
                    comparisons++;
                    charsChecked++;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) pos.add(i);

                if (steps != null && (canAddDetailedStep(steps) || match)) {
                    Map<String, Object> verifyState = new LinkedHashMap<>();
                    verifyState.put("Window Start Position (i)", i);
                    verifyState.put("Current Window Substring", windowStr);
                    verifyState.put("Pattern Hash (p)", p);
                    verifyState.put("Current Window Hash (t)", currentWindowHash);
                    verifyState.put("Hash Comparison", "t (" + currentWindowHash + ") == p (" + p + ") -> Verify characters");
                    verifyState.put("Characters Verified in Window", charsChecked + " / " + M);
                    verifyState.put("Verification Outcome", match ? "EXACT MATCH CONFIRMED at index " + i : "SPURIOUS HIT (Hash Collision — characters differ)");
                    verifyState.put("Total Character Comparisons", comparisons);
                    verifyState.put("Matching Positions Discovered", new ArrayList<>(pos));
                    addStep(steps, match ? "3. Hash Match & Character Verification (MATCH)" : "3. Hash Match & Character Verification (Collision)",
                            "Window i = " + i + ": Hash Match (t == p == " + p + ") -> " + (match ? "Confirmed Match" : "Hash Collision"),
                            match
                                    ? "Window hash matches pattern hash (" + p + "). Verified all " + M + " characters match '" + pattern + "'. Recorded match at index " + i + "."
                                    : "Window hash matches pattern hash (" + p + "), but character verification failed after " + charsChecked + " comparison(s) (spurious hit).",
                            verifyState);
                }
            }
            if (i < N - M) {
                char outChar = text.charAt(i);
                char inChar = text.charAt(i + M);
                int prevT = t;
                t = (d * (t - text.charAt(i) * h) + text.charAt(i + M)) % q;
                if (t < 0) t = (t + q);

                if (steps != null && canAddDetailedStep(steps)) {
                    Map<String, Object> rollState = new LinkedHashMap<>();
                    rollState.put("Transition", "Window " + i + " -> Window " + (i + 1));
                    rollState.put("Outgoing Leading Character text[" + i + "]", "'" + formatChar(outChar) + "' (ASCII " + (int) outChar + ")");
                    rollState.put("Incoming Trailing Character text[" + (i + M) + "]", "'" + formatChar(inChar) + "' (ASCII " + (int) inChar + ")");
                    rollState.put("Previous Window Hash", prevT);
                    rollState.put("Updated Rolling Hash (t)", t);
                    rollState.put("Next Window Substring", text.substring(i + 1, i + 1 + M));
                    addStep(steps, "4. Rolling-Hash Update",
                            "Roll Hash from Window " + i + " to " + (i + 1) + ": t = " + prevT + " -> " + t,
                            "Removed leading char '" + formatChar(outChar) + "' and added trailing char '" + formatChar(inChar) + "' in O(1) time: new window hash t = " + t + ".",
                            rollState);
                }
            }
        }

        res.setFound(!pos.isEmpty());
        res.setPositions(pos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("Hash Modulo: " + q);

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Pattern", pattern);
            finalState.put("Pattern Hash (p)", p);
            finalState.put("Hash Modulo (q)", q);
            finalState.put("Total Verification Comparisons", comparisons);
            finalState.put("Final Matching Positions", new ArrayList<>(pos));
            addStep(steps, "5. Final Result",
                    "Rabin-Karp Search Complete — " + pos.size() + " Match(es) Found",
                    "Completed Rabin-Karp rolling-hash scan with " + comparisons + " character verification comparison(s). Final matching positions: " + pos + ".",
                    finalState);
        }
        return res;
    }

    class ACTrieNode {
        int stateId;
        String pathLabel = "";
        ACTrieNode[] children = new ACTrieNode[256];
        ACTrieNode fail;
        List<Integer> output = new ArrayList<>();
    }

    public StringSearchResult ahoCorasickSearch(String text, List<String> patterns) {
        return ahoCorasickSearch(text, patterns, null);
    }

    public StringSearchResult ahoCorasickSearch(String text, List<String> patterns, List<WalkthroughStep> steps) {
        StringSearchResult res = new StringSearchResult();
        res.setAlgorithmName("Aho Corasick Search");
        res.setWhyUsed("Simultaneous multi-pattern matching using an Automaton and failure links.");
        res.setTimeComplexity("O(N+M+Z)");
        res.setSpaceComplexity("O(M*Σ)");

        Map<String, List<Integer>> multiPos = new HashMap<>();
        for (String p : patterns) {
            if (!p.trim().isEmpty()) multiPos.put(p.trim(), new ArrayList<>());
        }

        List<Integer> allPos = new ArrayList<>();
        int comparisons = 0;

        if (multiPos.isEmpty() || text.isEmpty()) {
            res.setFound(false);
            res.setPositions(allPos);
            return res;
        }

        List<String> validPatterns = new ArrayList<>(multiPos.keySet());

        // Build Trie
        int nextStateId = 0;
        ACTrieNode root = new ACTrieNode();
        root.stateId = nextStateId++;
        root.pathLabel = "ROOT";
        List<ACTrieNode> allNodes = new ArrayList<>();
        allNodes.add(root);

        for (int i = 0; i < validPatterns.size(); i++) {
            String p = validPatterns.get(i);
            ACTrieNode curr = root;
            StringBuilder prefix = new StringBuilder();
            for (char c : p.toCharArray()) {
                if (c >= 256) continue; // safety fallback for ASCII automaton
                prefix.append(c);
                if (curr.children[c] == null) {
                    ACTrieNode node = new ACTrieNode();
                    node.stateId = nextStateId++;
                    node.pathLabel = prefix.toString();
                    curr.children[c] = node;
                    allNodes.add(node);
                }
                curr = curr.children[c];
            }
            curr.output.add(i);

            if (steps != null) {
                Map<String, Object> trieState = new LinkedHashMap<>();
                trieState.put("Inserted Pattern #" + (i + 1), p);
                trieState.put("Terminal Automaton State", formatState(curr));
                trieState.put("Total Automaton States Created", allNodes.size());
                trieState.put("State Output Pattern", p);
                addStep(steps, "1. Trie Construction",
                        "Insert Pattern '" + p + "' -> Terminal " + formatState(curr),
                        "Inserted characters of pattern '" + p + "' into the Aho-Corasick trie, ending at " + formatState(curr) + ".",
                        trieState);
            }
        }

        // Build Failure Links using BFS
        Queue<ACTrieNode> q = new LinkedList<>();
        root.fail = root;
        for (int c = 0; c < 256; c++) {
            if (root.children[c] != null) {
                root.children[c].fail = root;
                q.add(root.children[c]);
            } else {
                root.children[c] = root;
            }
        }

        List<String> failureLinksSummary = new ArrayList<>();
        while (!q.isEmpty()) {
            ACTrieNode u = q.poll();
            for (int c = 0; c < 256; c++) {
                if (root != u.children[c] && u.children[c] != null) {
                    ACTrieNode v = u.children[c];
                    ACTrieNode failNode = u.fail;
                    while (failNode != root && failNode.children[c] == null) {
                        failNode = failNode.fail;
                    }
                    if (failNode.children[c] != null && failNode.children[c] != root) {
                        v.fail = failNode.children[c];
                    } else {
                        v.fail = root;
                    }
                    if (v.fail != null) {
                        v.output.addAll(v.fail.output);
                    }
                    failureLinksSummary.add(formatState(v) + " --fail--> " + formatState(v.fail));
                    q.add(v);
                }
            }
        }

        if (steps != null) {
            Map<String, Object> failState = new LinkedHashMap<>();
            failState.put("Patterns in Automaton", validPatterns);
            failState.put("Total States in Trie", allNodes.size());
            failState.put("Depth-1 States Failure Links", "All depth-1 states point to State #0 (ROOT)");
            failState.put("Computed Deeper Failure Links", failureLinksSummary.isEmpty() ? Collections.singletonList("None (single-character patterns only)") : failureLinksSummary);
            addStep(steps, "2. Failure Link Construction (BFS)",
                    "Build Automaton Failure Links via BFS (" + allNodes.size() + " States)",
                    "Constructed failure links across all trie states using Breadth-First Search and merged output sets along failure chains.",
                    failState);
        }

        // Search Text
        ACTrieNode curr = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 256) continue;
            comparisons++;

            ACTrieNode startState = curr;
            List<String> failTransitions = new ArrayList<>();
            while (curr != root && curr.children[c] == null) {
                ACTrieNode nextFail = curr.fail;
                failTransitions.add(formatState(curr) + " -> " + formatState(nextFail));
                curr = nextFail;
            }
            if (curr.children[c] != null && curr.children[c] != root) {
                curr = curr.children[c];
            } else {
                curr = root;
            }

            List<String> stepMatches = new ArrayList<>();
            for (int patIdx : curr.output) {
                String matchPat = validPatterns.get(patIdx);
                int startIdx = i - matchPat.length() + 1;
                multiPos.get(matchPat).add(startIdx);
                allPos.add(startIdx);
                stepMatches.add("'" + matchPat + "' at index " + startIdx);
            }

            boolean foundHere = !stepMatches.isEmpty();
            if (steps != null && (canAddDetailedStep(steps) || foundHere)) {
                Map<String, Object> scanState = new LinkedHashMap<>();
                scanState.put("Text Index (i)", i);
                scanState.put("Input Character text[" + i + "]", "'" + formatChar(c) + "'");
                scanState.put("Previous Automaton State", formatState(startState));
                scanState.put("Failure Transitions Followed", failTransitions.isEmpty() ? "None" : String.join(", ", failTransitions));
                scanState.put("New Automaton State", formatState(curr));
                scanState.put("Pattern Matches Discovered at Step", foundHere ? stepMatches : "None");
                scanState.put("All Match Positions So Far", new ArrayList<>(allPos));
                addStep(steps, foundHere ? "4. Pattern Match Discovered" : "3. Automaton State Transition",
                        "Read text[" + i + "] = '" + formatChar(c) + "': " + formatState(startState) + " -> " + formatState(curr) + (foundHere ? " (MATCH!)" : ""),
                        foundHere
                                ? "Transitioned to " + formatState(curr) + " on '" + formatChar(c) + "' and emitted match(es): " + String.join(", ", stepMatches) + "."
                                : "Processed text[" + i + "] = '" + formatChar(c) + "' and transitioned from " + formatState(startState) + " to " + formatState(curr) + ".",
                        scanState);
            }
        }

        Collections.sort(allPos);
        res.setFound(!allPos.isEmpty());
        res.setMultiPositions(multiPos);
        res.setPositions(allPos);
        res.setComparisons(comparisons);
        res.setPreprocessingInfo("Trie built with failure links for " + validPatterns.size() + " patterns.");

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Patterns Searched", validPatterns);
            finalState.put("Per-Pattern Match Positions", multiPos);
            finalState.put("All Matching Start Positions (Sorted)", new ArrayList<>(allPos));
            finalState.put("Total Characters Processed", comparisons);
            addStep(steps, "5. Final Result",
                    "Aho-Corasick Search Complete — " + allPos.size() + " Total Match(es)",
                    "Completed single-pass Aho-Corasick automaton scan across " + comparisons + " character(s). Final matching positions: " + allPos + ".",
                    finalState);
        }
        return res;
    }

    private String formatState(ACTrieNode node) {
        if (node == null) return "null";
        if (node.stateId == 0) return "State #0 (ROOT)";
        return "State #" + node.stateId + " (\"" + node.pathLabel + "\")";
    }

    private boolean canAddDetailedStep(List<WalkthroughStep> steps) {
        return steps != null && steps.size() < MAX_DETAILED_STEPS;
    }

    private String previewText(String text, int start, int len) {
        if (text == null || text.isEmpty()) return "";
        int end = Math.min(text.length(), Math.max(start + len, 60));
        String sub = text.substring(0, end).replace("\r", " ").replace("\n", " ");
        return end < text.length() ? sub + " ..." : sub;
    }

    private String extractWindow(String text, int alignStart, int patLen) {
        if (text == null || text.isEmpty()) return "";
        int winStart = Math.max(0, alignStart - 10);
        int winEnd = Math.min(text.length(), alignStart + Math.max(patLen, 1) + 15);
        String prefix = winStart > 0 ? "..." : "";
        String suffix = winEnd < text.length() ? "..." : "";
        String before = text.substring(winStart, Math.min(alignStart, text.length())).replace("\r", " ").replace("\n", " ");
        int matchEnd = Math.min(text.length(), alignStart + patLen);
        String aligned = text.substring(Math.min(alignStart, text.length()), matchEnd).replace("\r", " ").replace("\n", " ");
        String after = text.substring(matchEnd, winEnd).replace("\r", " ").replace("\n", " ");
        return prefix + before + "[" + aligned + "]" + after + suffix + " (indices " + winStart + ".." + (winEnd - 1) + ")";
    }

    private String formatZSlice(int[] Z, int uptoIdx) {
        int start = Math.max(0, uptoIdx - 12);
        int end = Math.min(Z.length - 1, uptoIdx);
        StringBuilder sb = new StringBuilder("[");
        if (start > 0) sb.append("..., ");
        for (int idx = start; idx <= end; idx++) {
            sb.append(idx).append(":").append(Z[idx]);
            if (idx < end) sb.append(", ");
        }
        if (end < Z.length - 1) sb.append(", ...");
        sb.append("]");
        return sb.toString();
    }

    private String formatChar(char c) {
        if (c == '\n') return "\\n";
        if (c == '\r') return "\\r";
        if (c == '\t') return "\\t";
        return String.valueOf(c);
    }

    private void addStep(List<WalkthroughStep> steps, String stage, String operation, String explanation, Map<String, Object> state) {
        steps.add(new WalkthroughStep(steps.size() + 1, stage, operation, explanation, state));
    }
}
