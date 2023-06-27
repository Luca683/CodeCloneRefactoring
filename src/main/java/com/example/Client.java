package com.example;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;

public class Client {
    private static final String FILE_PATH = "/Users/stran/OneDrive/Desktop/CodeCloneRefactoring/src/main/java/com/example/Car.java";
    public static void main(String[] args) throws Exception{
        
        CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH));
        
        Visitor v = new Visitor(cu);
        List<String> methodNameList = v.extractNameMethod();

        SimilarityAnalyzer analyzer = new SimilarityAnalyzer();

        /* to do list
         1. Tramite l'oggetto cu, estraggo una lista con tutti i blockStmt di ogni metodo
         2. Converto questa lista in una lista che contiene per ogni blocco il tipo dei suoi statement.
         3. Mi procuro poi un'istanza del analizzatore (che userà un determinato algoritmo al suo interno)
         4. L'analizzatore avrà un metodo che prende 2 liste e le confronta, quindi man mano gliele passo
         */
        
        List<BlockStmt> methodBlocksList = v.extractBlockStatementsMethod();

        List<String> statementTypeFirstList = new ArrayList<>();
        List<String> statementTypeSecondList = new ArrayList<>();
        
        for(int i=0;i<methodBlocksList.size();i++){
            statementTypeFirstList = Visitor.createListOfTypeStatement(methodBlocksList.get(i));
            for(int j=i+1;j<methodBlocksList.size();j++){
                statementTypeSecondList = Visitor.createListOfTypeStatement(methodBlocksList.get(j));
                System.out.println("Lista type statements method: "+methodNameList.get(i)+"()");
                v.printTypeStatement(methodBlocksList.get(i));
                System.out.println("Lista type statements method: "+methodNameList.get(j)+"()");
                v.printTypeStatement(methodBlocksList.get(j));
                double similarity = analyzer.calculateSimilarity(statementTypeFirstList, statementTypeSecondList);
                System.out.println("Analisi similarita': "+similarity+"%\n\n");
            }
        }
        

    }
}
