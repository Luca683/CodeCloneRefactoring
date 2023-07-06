package com.example;

import org.junit.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


public class TestVisitor {
    private static final String FILE_PATH = "/Users/stran/OneDrive/Desktop/CodeCloneRefactoring/src/main/java/com/example/Car.java";
    CompilationUnit cu;
    Visitor v;

    public TestVisitor() throws Exception{
        cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH)); //Prendiamo come classe di test sempre la classe Car.java
        v = new Visitor(cu);
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
        List<BlockStmt> methodBlocksList = v.extractBlockStatementsMethod();
        BlockStmt blockTest = createTestBlock();

        assertEquals(methodBlocksList.size(), 9);
        assertEquals(blockTest, methodBlocksList.get(3));
    }

    @Test
    public void testCreateListOfTypeStatements(){
        List<BlockStmt> methodBlocksList = v.extractBlockStatementsMethod();
        
        List<String> typeOfBlockStmtList = Visitor.createListOfTypeStatement(methodBlocksList.get(7));   

        List<String> expectedList = new ArrayList<>();
        expectedList.add("MethodCallExpr");
        expectedList.add("ForStmt");
        expectedList.add("ForStmt");
        expectedList.add("VariableDeclarationExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndForStmt");
        expectedList.add("VariableDeclarationExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndForStmt");
        expectedList.add("VariableDeclarationExpr");
        expectedList.add("DoStmt");
        expectedList.add("UnaryExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndDoStmt");
        expectedList.add("VariableDeclarationExpr");
        expectedList.add("WhileStmt");
        expectedList.add("AssignExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndWhileStmt");
        expectedList.add("VariableDeclarationExpr");
        expectedList.add("SwitchStmt");
        expectedList.add("CaseStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("BreakStmt");
        expectedList.add("CaseStmt");
        expectedList.add("IfStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndIfStmt");
        expectedList.add("ElseStmt");
        expectedList.add("IfStmt");
        expectedList.add("AssignExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndIfStmt");
        expectedList.add("ElseStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndElseStmt");
        expectedList.add("EndElseStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("BreakStmt");
        expectedList.add("CaseStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("BreakStmt");
        expectedList.add("DefaultStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndSwitchStmt");
        expectedList.add("VariableDeclarationExpr");
        expectedList.add("ForEachStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndForEachStmt");
        expectedList.add("TryStmt");
        expectedList.add("VariableDeclarationExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndTryStmt");
        expectedList.add("CatchClause");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndCatchClause");
        expectedList.add("CatchClause");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndCatchClause");
        expectedList.add("FinallyStmt");
        expectedList.add("MethodCallExpr");
        expectedList.add("MethodCallExpr");
        expectedList.add("EndFinallyStmt");
        expectedList.add("ReturnStmt");

        assertEquals(typeOfBlockStmtList, expectedList);
    }
}


