package com.documentcategorization.datastructures;
import com.documentcategorization.model.TermFrequency;
import java.util.ArrayList;
import java.util.List;
public class TermFrequencyMap {
    private static class HashNode {
        String key;
        int value;
        HashNode next;
        public HashNode(String key, int value) { this.key = key; this.value = value; }
    }
    private HashNode[] buckets;
    private int capacity;
    public TermFrequencyMap(int capacity) {
        this.capacity = capacity;
        this.buckets = new HashNode[capacity];
    }
    private int getIndex(String key) {
        return Math.abs(key.hashCode() % capacity);
    }
    public void increment(String key) {
        int index = getIndex(key);
        HashNode head = buckets[index];
        while (head != null) {
            if (head.key.equals(key)) { head.value++; return; }
            head = head.next;
        }
        HashNode newNode = new HashNode(key, 1);
        newNode.next = buckets[index];
        buckets[index] = newNode;
    }
    public List<TermFrequency> toList() {
        List<TermFrequency> list = new ArrayList<>();
        for (HashNode node : buckets) {
            while (node != null) {
                list.add(new TermFrequency(node.key, node.value));
                node = node.next;
            }
        }
        return list;
    }
}
