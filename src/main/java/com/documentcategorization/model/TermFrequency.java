package com.documentcategorization.model;

public class TermFrequency implements Comparable<TermFrequency> {
    private String term;
    private int frequency;

    public TermFrequency(String term, int frequency) {
        this.term = term;
        this.frequency = frequency;
    }

    public String getTerm() {
        return term;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(TermFrequency other) {
        int cmp = Integer.compare(other.frequency, this.frequency); // Descending by frequency
        if (cmp != 0) {
            return cmp;
        }
        if (this.term == null && other.term == null) return 0;
        if (this.term == null) return 1;
        if (other.term == null) return -1;
        return this.term.compareTo(other.term); // Lexicographical tie-breaker
    }
}
