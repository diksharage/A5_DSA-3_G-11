package com.documentcategorization.model;
public class TermFrequency implements Comparable<TermFrequency> {
    private String term;
    private int frequency;
    public TermFrequency(String term, int frequency) { this.term = term; this.frequency = frequency; }
    public String getTerm() { return term; }
    public int getFrequency() { return frequency; }
    @Override
    public int compareTo(TermFrequency other) {
        return Integer.compare(other.frequency, this.frequency); // Descending
    }
}
