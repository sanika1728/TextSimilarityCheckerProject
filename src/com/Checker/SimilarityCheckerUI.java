package com.Checker;

import com.Checker.Utils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class SimilarityCheckerUI extends JFrame {
    private JTextArea outputArea;
    private File file1, file2, stopwordsFile;

    public SimilarityCheckerUI() {
        setTitle("Text Similarity Checker (Plagiarism Detector)");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton selectFile1Btn = new JButton("Select File 1");
        JButton selectFile2Btn = new JButton("Select File 2");
        JButton selectStopwordsBtn = new JButton("Select Stopwords File");
        JButton computeBtn = new JButton("Compute Similarity");
        JButton exportPDFBtn = new JButton("Export Result as PDF");

        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        topPanel.add(selectFile1Btn);
        topPanel.add(selectFile2Btn);
        topPanel.add(selectStopwordsBtn);
        topPanel.add(computeBtn);
        topPanel.add(exportPDFBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        selectFile1Btn.addActionListener(e -> file1 = chooseFile());
        selectFile2Btn.addActionListener(e -> file2 = chooseFile());
        selectStopwordsBtn.addActionListener(e -> stopwordsFile = chooseFile());

        computeBtn.addActionListener(e -> {
            if (file1 == null || file2 == null) {
                showMessage("Please select both input files.");
                return;
            }

            try {
                String text1 = new String(Files.readAllBytes(file1.toPath()));
                String text2 = new String(Files.readAllBytes(file2.toPath()));

                Set<String> stopwords = new HashSet<>();
                if (stopwordsFile != null) {
                    stopwords = Utils.loadStopwords(stopwordsFile.getPath());
                }

                List<String> tokens1 = Utils.preprocessText(text1, stopwords);
                List<String> tokens2 = Utils.preprocessText(text2, stopwords);

                Map<String, Integer> freq1 = Utils.getWordFrequencies(tokens1);
                Map<String, Integer> freq2 = Utils.getWordFrequencies(tokens2);

                double similarity = Utils.calculateCosineSimilarity(freq1, freq2);
                Set<String> commonWords = Utils.findCommonWords(freq1, freq2);

                StringBuilder result = new StringBuilder();
                result.append("Similarity: ").append(String.format("%.2f", similarity)).append("%\n\n");
                result.append("Common Words:\n").append(commonWords);

                outputArea.setText(result.toString());
                Utils.writeToFile("output.txt", result.toString());

            } catch (IOException ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        exportPDFBtn.addActionListener(e -> {
            if (outputArea.getText().isEmpty()) {
                showMessage("Please generate similarity result first.");
                return;
            }

            try {
                String pdfName = "similarity_result.pdf";
                PDFExporter.export(outputArea.getText(), pdfName);

             if (Desktop.isDesktopSupported()) {
                 Desktop.getDesktop().open(new File(pdfName));
             } else {
                 showMessage("PDF saved but auto-open is not supported on this system.");
             }

            } catch (Exception ex) {
                showMessage("Failed to export PDF: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private File chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        return (option == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimilarityCheckerUI::new);
    }
}

