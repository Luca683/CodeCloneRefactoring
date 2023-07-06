package com.example;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestSimilarityAnalyzer {
    List<String> l1;
    List<String> l2;
    int distance;
    double similarity;

    public TestSimilarityAnalyzer(){
        l1 = new ArrayList<>();
        l2 = new ArrayList<>();
    }
    @Test
    public void testLevenshteinDistance() {
        List<String> l1 = new ArrayList<>();
        List<String> l2 = new ArrayList<>();

        // Con liste uguali mi aspetto che la distanza tra esse sia 0
        l1 = Arrays.asList("Black", "Red", "Blue");
        l2 = Arrays.asList("Black", "Red", "Blue");
        distance = SimilarityAnalyzer.levenshteinDistance(l1, l2);
        assertEquals(0, distance);

        // Se ho due liste che si differenziano di qualche elemento allora mi aspetto che la distanza sia uguale 
        // al numero di operazioni di inserimento, cancellazione e sostituzione da fare per far si che le liste abbiano stessi elementi
        l1 = Arrays.asList("Black", "Red", "Blue");
        l2 = Arrays.asList("Yellow", "Red", "Blue", "Gray");
        distance = SimilarityAnalyzer.levenshteinDistance(l1, l2);
        assertEquals(2, distance); //Mi aspetto: Yellow -> Black e canc(Gray)
    }

    @Test
    public void testCalculateSimilarity() {
        SimilarityAnalyzer analyzer = new SimilarityAnalyzer();

        // Liste uguali
        l1 = Arrays.asList("Black", "Red", "Blue");
        l2 = Arrays.asList("Black", "Red", "Blue");
        similarity = analyzer.calculateSimilarity(l1, l2);
        assertEquals(100.0, similarity, 0.0001);

        // Liste diverse
        l1 = Arrays.asList("Black", "Red", "Blue");
        l2 = Arrays.asList("Yellow", "Red", "Blue", "Gray");
        similarity = analyzer.calculateSimilarity(l1, l2);
        assertEquals(50.0, similarity, 0.0001); //((maxLength - distance)/maxLength) * 100 = ((4-2)/4) * 100 = 50
    }
}
