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
import com.github.javaparser.ast.stmt.IfStmt;


public class oldVisitor{
    private static final String FILE_PATH = "/Users/stran/OneDrive/Desktop/CodeCloneRefactoring/src/main/java/com/example/Car.java";
    
    public static void main(String[] args) throws Exception{
        
        CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH));
        
        //Visitatore dei nomi di ogni metodo
        List<String> methodNameList = new ArrayList<>();
        VoidVisitor<List<String>> methodNameVisitor = new MethodName();
        methodNameVisitor.visit(cu, methodNameList);

        //Visitatore degli statements di ogni metodo
        List<BlockStmt> methodBlocksList = new ArrayList<>();
        VoidVisitor<List<BlockStmt>> methodBlockStmtVisitor = new MethodBlockStmt();
        methodBlockStmtVisitor.visit(cu, methodBlocksList);

        //print..
        //NOTA: cominciamo ad ispezionare la lista methodBlocksList dall'indice 1, perchè l'elemento in posizione 0 conterrà il BlockStmt relativo al costruttore (che a noi non interessa)
        for(int i=1;i<methodBlocksList.size();i++){ //.. for each block i ...
            System.out.println("METHOD NAME: " + methodNameList.get(i-1));
            for(int j=0;j<methodBlocksList.get(i).getStatements().size();j++){ //... for each statement j of block i
                //Se il j-esimo statement è un IF
                if(methodBlocksList.get(i).getStatements().get(j).isIfStmt()){ 
                    IfStmt ifStatement = methodBlocksList.get(i).getStatements().get(j).asIfStmt();
                    System.out.println(j + " if: " + ifStatement.getCondition());
                    System.out.println("then: ");
                    
                    //ramo THEN
                    for(int h=0;h<ifStatement.getThenStmt().asBlockStmt().getStatements().size();h++){
                        System.out.println("\t" + j + "." + h + " " + ifStatement.getThenStmt().asBlockStmt().getStatements().get(h));
                    }
                    
                    //ramo ELSE (se presente)
                    if(ifStatement.hasElseBlock()){
                        System.out.println("else:");
                        for(int h=0;h<ifStatement.getElseStmt().get().asBlockStmt().getStatements().size();h++){
                            System.out.println("\t" + j + "." + h + " " + ifStatement.getElseStmt().get().asBlockStmt().getStatements().get(h));
                        }
                    }
                    
                }
                else System.out.println(j + ". " + methodBlocksList.get(i).getStatements().get(j));
            }
            System.out.println("\n");
        }
        
    }

    private static class MethodName extends VoidVisitorAdapter<List<String>>{
        @Override
        public void visit(MethodDeclaration md, List<String> collector){
            collector.add(md.getNameAsString());
        }
    }

    private static class MethodBlockStmt extends VoidVisitorAdapter<List<BlockStmt>>{
        @Override
        public void visit(BlockStmt bs, List<BlockStmt> collector){
            collector.add(bs);
        }
    }
    
}