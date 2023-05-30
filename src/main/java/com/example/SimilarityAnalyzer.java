package com.example;

import java.util.List;

public class SimilarityAnalyzer {
    
    public static int levenshteinDistance(List<String> m1, List<String> m2){
        int[][] matrix = new int[m1.size() + 1][m2.size() + 1];

        for(int i=0;i<=m1.size();i++) matrix[i][0] = i;
        for(int j=0;j<=m2.size();j++) matrix[0][j] = j;

        int cost;
        for(int i=1;i<=m1.size();i++){
            for(int j=1;j<=m2.size();j++){
                if(m1.get(i-1).equals(m2.get(j-1))) cost = 0;
                else cost = 1;
                matrix[i][j] = Math.min(matrix[i-1][j] + 1, Math.min(matrix[i][j-1] + 1, matrix[i-1][j-1] + cost));
            }
        }

        return matrix[m1.size()][m2.size()];
    }

    public double calculateSimilarity(List<String> m1, List<String> m2){
        int distance = levenshteinDistance(m1, m2);
        int maxLength = Math.max(m1.size(), m2.size());
        
        return ((double) (maxLength - distance) / maxLength) * 100;
    }
}
