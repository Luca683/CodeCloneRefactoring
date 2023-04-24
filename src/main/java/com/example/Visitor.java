package com.example;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.stmt.BlockStmt;


public class Visitor{
    private static final String FILE_PATH = "/Users/stran/OneDrive/Desktop/CodeCloneRefactoring/src/main/java/com/example/Car.java";

    public static void main(String[] args) throws Exception{
        
        CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH));

        List<BlockStmt> methodBlocks = new ArrayList<>();
        VoidVisitor<List<BlockStmt>> methodBlockStmt = new MethodBlockStmt();
        methodBlockStmt.visit(cu, methodBlocks);

        for(int i=0;i<methodBlocks.size();i++){ //for each block i ...
            for(int j=0;j<methodBlocks.get(i).getStatements().size();j++){ //... for each statement j of block i
                System.out.println(j + ". " + methodBlocks.get(i).getStatements().get(j));
            }
            System.out.println("\n");
        }
        
    }

    private static class MethodBlockStmt extends VoidVisitorAdapter<List<BlockStmt>>{
        @Override
        public void visit(BlockStmt md, List<BlockStmt> collector){
            collector.add(md);
        }
    }
    
}