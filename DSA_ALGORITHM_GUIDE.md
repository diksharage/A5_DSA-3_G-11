# DSA Algorithm Guide

This project implements five core string matching algorithms to identify terms within document text. No built-in `indexOf` or regex libraries are used for the algorithmic matching.

## 1. Naive String Matching
- **Purpose**: Baseline string search.
- **Why Used**: To establish a baseline for comparison against optimized algorithms.
- **Time Complexity**: O((N-M+1) * M)
- **Space Complexity**: O(1)

## 2. KMP (Knuth-Morris-Pratt)
- **Purpose**: Efficient matching by avoiding redundant comparisons.
- **Why Used**: Best for general-purpose matching where patterns have repeating sub-patterns.
- **Time Complexity**: O(N + M)
- **Space Complexity**: O(M) for the LPS (Longest Prefix Suffix) array.

## 3. Z Algorithm
- **Purpose**: Linear time pattern matching using the Z-array.
- **Why Used**: Efficiently finds all occurrences by creating a combined `Pattern$Text` string and finding Z-values matching pattern length.
- **Time Complexity**: O(N + M)
- **Space Complexity**: O(N + M) for the Z-array.

## 4. Rabin-Karp
- **Purpose**: Hash-based string matching.
- **Why Used**: Particularly useful when searching for multiple patterns of the same length by comparing rolling hashes.
- **Time Complexity**: O(N + M) average, O(N * M) worst-case.
- **Space Complexity**: O(1)

## 5. Aho-Corasick
- **Purpose**: Simultaneous multiple pattern searching.
- **Why Used**: Essential for finding *multiple* different features across a document in a single pass using a Trie and failure links.
- **Time Complexity**: O(N + M + Z) where Z is the number of matches.
- **Space Complexity**: O(M * K) where K is the alphabet size.
