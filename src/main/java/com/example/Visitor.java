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
import com.github.javaparser.ast.stmt.WhileStmt;

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
    /*private static String getStatementType(Statement st){
        if(st.isExpressionStmt()){
            ExpressionStmt exp = (ExpressionStmt) st;
            return exp.getExpression().getClass().getSimpleName();
        }
        else return st.getClass().getSimpleName();
    }*/

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
            ForStmt forStmt = (ForStmt) st;
            ForStmtVisitor visitor = new ForStmtVisitor(list);
            visitor.visit(forStmt, null);
        }
        else if(st.isWhileStmt()){
            WhileStmt whileStmt = (WhileStmt) st;
            WhileStmtVisitor visitor = new WhileStmtVisitor(list);
            visitor.visit(whileStmt, null);
        }

        else list.add(st.getClass().getSimpleName());
    }

    //Dato un blocco di statement, crea una lista di stringhe contenente per ogni statement la sua tipologia 
    public List<String> createListOfTypeStatement(BlockStmt block){
        List<String> statementTypeList = new ArrayList<>();
        for(int i=0;i<block.getStatements().size();i++){
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
            Statement body = forStmt.getBody();
            BlockStmt forBlock = body.asBlockStmt();
            list.add("ForStmt");
            for (int i=0;i<forBlock.getStatements().size();i++) {
                addTypeStatementInList(list, forBlock.getStatements().get(i));
            }
            list.add("EndFor");
        }
    }

    private static class WhileStmtVisitor extends VoidVisitorAdapter<Void>{
        private List<String> list;

        public WhileStmtVisitor(List<String> list) {
            this.list = list;
        }

        @Override
        public void visit(WhileStmt whileStmt, Void arg) {
            Statement body = whileStmt.getBody();
            BlockStmt whileBlock = body.asBlockStmt();
            list.add("whileStmt");
            for (int i=0;i<whileBlock.getStatements().size();i++) {
                addTypeStatementInList(list, whileBlock.getStatements().get(i));
            }
            list.add("EndWhile");
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
