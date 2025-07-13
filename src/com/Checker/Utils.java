package com.Checker;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.nio.file.Files;

public class Utils {

    public static Set<String> loadStopwords(String filename) throws IOException {
        Set<String> stopwords = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            stopwords.add(line.trim().toLowerCase());
        }
        br.close();
        return stopwords;
    }

    public static List<String> preprocessText(String text, Set<String> stopwords) {
        text = text.toLowerCase().replaceAll("[^a-z\\s]", " ");
        String[] words = text.split("\\s+");
        List<String> filtered = new ArrayList<>();
        for (String word : words) {
            if (!stopwords.contains(word) && !word.isEmpty()) {
                filtered.add(word);
            }
        }
        return filtered;
    }

    public static Map<String, Integer> getWordFrequencies(List<String> words) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
    }

    public static double calculateCosineSimilarity(Map<String, Integer> f1, Map<String, Integer> f2) {
        Set<String> allWords = new HashSet<>();
        allWords.addAll(f1.keySet());
        allWords.addAll(f2.keySet());

        int dotProduct = 0;
        double normA = 0.0, normB = 0.0;

        for (String word : allWords) {
            int x = f1.getOrDefault(word, 0);
            int y = f2.getOrDefault(word, 0);
            dotProduct += x * y;
            normA += Math.pow(x, 2);
            normB += Math.pow(y, 2);
        }

        if (normA == 0 || normB == 0) return 0.0;
        return (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))) * 100;
    }

    public static Set<String> findCommonWords(Map<String, Integer> f1, Map<String, Integer> f2) {
        Set<String> common = new HashSet<>(f1.keySet());
        common.retainAll(f2.keySet());
        return common;
    }

    public static void writeToFile(String filename, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(content);
        writer.close();
    }
}

