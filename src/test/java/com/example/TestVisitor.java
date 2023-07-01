package com.example;

import org.junit.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.List;


public class TestVisitor {
    private static final String FILE_PATH = "/Users/stran/OneDrive/Desktop/CodeCloneRefactoring/src/main/java/com/example/Car.java";
    CompilationUnit cu;

    public TestVisitor() throws Exception{
        cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH)); //Prendiamo come classe di test sempre la classe Car.java
    }

    @Test
    public void testExtractNameMethod() {
        // Crea un'istanza di MethodNameExtractor
        Visitor v = new Visitor(cu);
        

        // Chiama il metodo extractNameMethod per ottenere la lista dei nomi dei metodi
        List<String> methodNames =  v.extractNameMethod();

        // Verifica il risultato atteso
        assertEquals(9, methodNames.size());
        assertEquals("turnOn", methodNames.get(0));
        assertEquals("turnOff", methodNames.get(1));
    }

    
}


