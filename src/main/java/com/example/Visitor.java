package com.example;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

public class Visitor {
    private CompilationUnit cu;

    public Visitor(CompilationUnit cu){
        this.cu = cu;
    }

    public List<String> extractNameMethod(){
        List<String> methodNameList = new ArrayList<>();
        VoidVisitor<List<String>> methodNameVisitor = new MethodName();
        methodNameVisitor.visit(cu, methodNameList);
        return methodNameList;
    }

    public List<BlockStmt> extractBlockStatementsMethod(){
        List<BlockStmt> methodBlocksList = new ArrayList<>();
        VoidVisitor<List<BlockStmt>> methodBlockStmtVisitor = new MethodBlockStmt();
        methodBlockStmtVisitor.visit(cu, methodBlocksList);
        return methodBlocksList;
    }

    private static String getStatementType(Statement st){
        if(st.isExpressionStmt()){
            ExpressionStmt exp = (ExpressionStmt) st;
            return exp.getExpression().getClass().getSimpleName();
        }
        else return st.getClass().getSimpleName();
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
