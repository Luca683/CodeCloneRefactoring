package com.example;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.stmt.BlockStmt;


public class Visitor{
    private static final String FILE_PATH = "/Users/stran/OneDrive/Desktop/CodeCloneRefactoring/src/main/java/com/example/Car.java";

    public static void main(String[] args) throws Exception{
        
        CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH));
        
        System.out.println("\t***FIRST VISITOR***");
        //For First Visitor
        VoidVisitor<Void> methodNameVisitor = new MethodNamePrinter();
        methodNameVisitor.visit(cu, null);

        System.out.println("\n\n\t***SECOND VISITOR***");
        //For Second Visitor
        List<String> methodNames = new ArrayList<>();
        VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
        methodNameCollector.visit(cu, methodNames);
        methodNames.forEach(n -> System.out.println("Method name collected: " + n));

        System.out.println("\n\n\t***THIRD VISITOR***");
        //For Third Visitor
        List<BlockStmt> methodStatements = new ArrayList<>();
        VoidVisitor<List<BlockStmt>> methodStatementsCollector= new MethodStatementsCollector();
        methodStatementsCollector.visit(cu, methodStatements);
            //Print a particular statement (getStatements) for a particular method (get)
        //System.out.println(methodStatements.get(2).getStatements().get(1));

        for(int i=0;i<methodStatements.size();i++){
            System.out.println("METHOD NAME: " + methodNames.get(i));
            System.out.println("STATEMENTS: " + methodStatements.get(i).getStatements());
            System.out.println("***********************************************");
        }
    }

    //First Visitor: print all Method Name of my class example "Car"
    private static class MethodNamePrinter extends VoidVisitorAdapter<Void>{
        
        //We want to override the visit method of class VoidVisitorAdapter, for the type we are interested in (MethodDeclaration) 
        @Override
        public void visit(MethodDeclaration md, Void arg){
            super.visit(md, arg);
            System.out.println("Method Name: " + md.getName());
        }
    }

    //Second Visitor: we want to collect in a list all Method name of our class example "Car"
    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>>{

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }

    //Third Visitor: we want to collect all statements for all method in a List
    private static class MethodStatementsCollector extends VoidVisitorAdapter<List<BlockStmt>>{
        @Override
        public void visit(MethodDeclaration md, List<BlockStmt> collector){
            super.visit(md, collector);
            collector.add(md.getBody().get());
        }
    }
}