package com.example;

import org.junit.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.List;


public class TestVisitor {
    private static final String FILE_PATH = "/Users/stran/OneDrive/Desktop/CodeCloneRefactoring/src/main/java/com/example/Car.java";
    CompilationUnit cu;

    public TestVisitor() throws Exception{
        cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH)); //Prendiamo come classe di test sempre la classe Car.java
    }

    private BlockStmt createTestBlock(){
        // Creazione dell'oggetto ReturnStmt con l'identificatore "color"
        ReturnStmt returnStatement = new ReturnStmt(new NameExpr("color"));

        // Creazione del blocco contenente l'istruzione di return
        BlockStmt blockStmt = new BlockStmt();
        blockStmt.addStatement(returnStatement);

        return blockStmt;
    }

    @Test
    public void testExtractNameMethod() {
        // Crea un'istanza di MethodNameExtractor
        Visitor v = new Visitor(cu);
        

        // Chiama il metodo extractNameMethod per ottenere la lista dei nomi dei metodi
        List<String> methodNames =  v.extractNameMethod();

        // Verifica il risultato atteso
        assertEquals(methodNames.size(), 9);
        assertEquals("turnOn", methodNames.get(0));
        assertEquals("turnOff", methodNames.get(1));
        assertEquals("printCarInfo", methodNames.get(6));
    }

    @Test
    public void testExtractBlockStatementsMethod(){
        Visitor v = new Visitor(cu);

        List<BlockStmt> methodBlocksList = v.extractBlockStatementsMethod();
        BlockStmt blockTest = createTestBlock();

        assertEquals(methodBlocksList.size(), 9);
        assertEquals(blockTest, methodBlocksList.get(3));
    }
    
}


