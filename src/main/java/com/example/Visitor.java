package com.example;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
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

    //Dato uno statement ne restituisce la tipologia sotto forma di stringa
    private static String getStatementType(Statement st){
        if(st.isExpressionStmt()){
            ExpressionStmt exp = (ExpressionStmt) st;
            return exp.getExpression().getClass().getSimpleName();
        }
        else return st.getClass().getSimpleName();
    }

    /*Riceve una lista e uno statement (che può essere un'espressione semplice, o complessa come un IfStmt, ForStmt e così via)
        Se lo statement è un'espressione semplice allora nella lista inserisco solo la tipologia di essa.
        Se lo statement è un'espressione complessa allora nella lista inserirò la tipologia di ogni statement annidato
        Il metodo è void perchè modifica solo la lista in input.
        Il metodo è ricorsivo. 
    */
    public static void addTypeStatementInList(List<String> list, Statement st){
        //Caso base
        if(st.isExpressionStmt()){
            ExpressionStmt exp = (ExpressionStmt) st;
            list.add(exp.getExpression().getClass().getSimpleName());
        }
        //Casi induttivi
        else if(st.isForStmt()){
            list.add("ForStmt");
            ForStmt forStmt = (ForStmt) st;
            ForStmtVisitor visitor = new ForStmtVisitor(list);
            visitor.visit(forStmt, null);
            list.add("EndFor");
        }

        else list.add(st.getClass().getSimpleName());
    }

    //Dato un blocco di statement, crea una lista di stringhe contenente per ogni statement la sua tipologia 
    public List<String> createListOfTypeStatement(BlockStmt block){
        List<String> statementTypeList = new ArrayList<>();
        for(int i=0;i<block.getStatements().size();i++){
            //statementTypeList.add(getStatementType(block.getStatements().get(i))); 
            addTypeStatementInList(statementTypeList, block.getStatements().get(i));    
        }
        return statementTypeList;
    }

    public void printTypeStatement(BlockStmt block){
        List<String> myList = createListOfTypeStatement(block);
        System.out.print("[ ");
        for(int i=0;i<myList.size();i++){
            System.out.print(myList.get(i)+" ");
        }
        System.out.println("]");
    }

    private static class ForStmtVisitor extends VoidVisitorAdapter<Void>{
        private List<String> list;

        public ForStmtVisitor(List<String> list) {
            this.list = list;
        }

        @Override
        public void visit(ForStmt forStmt, Void arg) {
            super.visit(forStmt, arg);
            Statement body = forStmt.getBody();

            if (body.isBlockStmt()) {
                BlockStmt forBlock = body.asBlockStmt();
                List<Statement> statements = forBlock.getStatements();

                for (Statement statement : statements) {
                    addTypeStatementInList(list, statement);
                }
            } else addTypeStatementInList(list, body);
            
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
