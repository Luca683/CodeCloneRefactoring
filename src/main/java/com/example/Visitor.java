package com.example;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;

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

    public static void scanComplexStatement(Statement st, List<String> list){
        String statementType = st.getClass().getSimpleName();
        BlockStmtVisitor visitor = new BlockStmtVisitor(list); 
        BlockStmt blockStmt = null;
        list.add(statementType);
        if(st.isForStmt()) blockStmt = st.asForStmt().getBody().asBlockStmt();
        else if(st.isWhileStmt()) blockStmt = st.asWhileStmt().getBody().asBlockStmt();
        else if(st.isForEachStmt()) blockStmt = st.asForEachStmt().getBody().asBlockStmt();
        else if(st.isDoStmt()) blockStmt = st.asDoStmt().getBody().asBlockStmt();
        else if(st.isIfStmt()) blockStmt = st.asIfStmt().getThenStmt().asBlockStmt();
        else if(st.isTryStmt()) blockStmt = st.asTryStmt().getTryBlock();
        if(blockStmt != null) visitor.visit(blockStmt, null);
        list.add("End"+statementType);

        if(st.isIfStmt()){
            if(st.asIfStmt().hasElseBranch()) {
                list.add("ElseStmt");
                Statement elseStmt = st.asIfStmt().getElseStmt().get();
                if(elseStmt.isBlockStmt()) {
                    visitor.visit(elseStmt.asBlockStmt(), null);
                } else {
                    addTypeStatementInList(list, elseStmt);
                }
                list.add("EndElseStmt");
            }
        }
        else if(st.isTryStmt()){
            //catch
            NodeList<CatchClause> catchBlocks = st.asTryStmt().getCatchClauses();
            for(int i=0;i<catchBlocks.size();i++){
                list.add("CatchClause");
                blockStmt = catchBlocks.get(i).getBody().asBlockStmt();
                visitor.visit(blockStmt, null);
                list.add("EndCatchClause");
            }
            //finally
            BlockStmt finallyBlock = st.asTryStmt().getFinallyBlock().orElse(null);
            if(finallyBlock != null){
                list.add("FinallyStmt");
                visitor.visit(finallyBlock, null);
                list.add("EndFinallyStmt");
            }
        }
    }

    public static void addTypeStatementInList(List<String> list, Statement st){
        if(st.isExpressionStmt()){
            ExpressionStmt exp = (ExpressionStmt) st;
            list.add(exp.getExpression().getClass().getSimpleName());
        }
        else if(st.isSwitchStmt()){
            list.add("SwitchStmt");
            NodeList<SwitchEntry> caseSwitch = st.asSwitchStmt().getEntries();
            SwitchEntryVisitor visitor = new SwitchEntryVisitor(list);
            visitor.visitListSwitchEntry(caseSwitch, null);
            list.add("EndSwitch");
        }
        else{
            scanComplexStatement(st, list);
        }

    }
    /*Riceve una lista e uno statement (che può essere un'espressione semplice, o complessa come un IfStmt, ForStmt e così via)
        Se lo statement è un'espressione semplice allora nella lista inserisco solo la tipologia di essa.
        Se lo statement è un'espressione complessa allora nella lista inserirò la tipologia di ogni statement annidato
        Il metodo è void perchè modifica solo la lista in input.
        Il metodo è ricorsivo. 
    */
    /*
    public static void addTypeStatementInList2(List<String> list, Statement st){
        //Caso base
        if(st.isExpressionStmt()){
            ExpressionStmt exp = (ExpressionStmt) st;
            list.add(exp.getExpression().getClass().getSimpleName());
        }
        //Casi induttivi
        else if(st.isForStmt()){
            list.add("ForStmt");
            BlockStmt blockStmt = st.asForStmt().getBody().asBlockStmt(); //Estraggo il blocco relativo al ciclo for
            BlockStmtVisitor visitor = new BlockStmtVisitor(list); 
            visitor.visit(blockStmt, null); //Visito il blocco estratto e aggiungo alla lista tutti i tipo dei suoi statements
            list.add("EndFor");
        }
        else if(st.isWhileStmt()){
            list.add("WhileStmt");
            BlockStmt blockStmt = st.asWhileStmt().getBody().asBlockStmt();
            BlockStmtVisitor visitor = new BlockStmtVisitor(list); 
            visitor.visit(blockStmt, null);
            list.add("EndWhile");
        }
        else if(st.isForEachStmt()){
            list.add("ForEachStmt");
            BlockStmt blockStmt = st.asForEachStmt().getBody().asBlockStmt();
            BlockStmtVisitor visitor = new BlockStmtVisitor(list);
            visitor.visit(blockStmt, null);
            list.add("EndForEach");
        }
        else if(st.isDoStmt()){
            list.add("DoStmt");
            BlockStmt blockStmt = st.asDoStmt().getBody().asBlockStmt();
            BlockStmtVisitor visitor = new BlockStmtVisitor(list);
            visitor.visit(blockStmt, null);
            list.add("EndDo");
        }
        else if(st.isIfStmt()){
            list.add("IfStmt");
            BlockStmt blockStmt = st.asIfStmt().getThenStmt().asBlockStmt();
            BlockStmtVisitor visitor = new BlockStmtVisitor(list);
            visitor.visit(blockStmt, null);
            list.add("EndIf");
            if(st.asIfStmt().hasElseBranch()) {
                list.add("ElseStmt");
                Statement elseStmt = st.asIfStmt().getElseStmt().get();
                if(elseStmt.isBlockStmt()) {
                    visitor.visit(elseStmt.asBlockStmt(), null);
                } else {
                    addTypeStatementInList(list, elseStmt);
                }
                list.add("EndElse");
            }
        }
        else if(st.isSwitchStmt()){
            list.add("SwitchStmt");
            NodeList<SwitchEntry> caseSwitch = st.asSwitchStmt().getEntries();
            SwitchEntryVisitor visitor = new SwitchEntryVisitor(list);
            visitor.visitListSwitchEntry(caseSwitch, null);
            list.add("EndSwitch");
        }
        else if(st.isTryStmt()){
            //try
            list.add("TryStmt");
            BlockStmt blockStmt = st.asTryStmt().getTryBlock();
            BlockStmtVisitor visitor = new BlockStmtVisitor(list);
            visitor.visit(blockStmt, null);
            list.add("EndTryStmt");
            //catch
            NodeList<CatchClause> catchBlocks = st.asTryStmt().getCatchClauses();
            for(int i=0;i<catchBlocks.size();i++){
                list.add("CatchClause");
                blockStmt = catchBlocks.get(i).getBody().asBlockStmt();
                visitor.visit(blockStmt, null);
                list.add("EndCatchClause");
            }
            //finally
            BlockStmt finallyBlock = st.asTryStmt().getFinallyBlock().orElse(null);
            if(finallyBlock != null){
                list.add("FinallyStmt");
                visitor.visit(finallyBlock, null);
                list.add("EndFinallyStmt");
            }  
        } 
        else list.add(st.getClass().getSimpleName()); //Da togliere appena avrò considerato tutti i possibili casi
    }
    */
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
            System.out.print(myList.get(i));
            if(i<myList.size()-1) System.out.print(", ");
        }
        System.out.println(" ]");
    }

    private static class BlockStmtVisitor extends VoidVisitorAdapter<Void>{
        private List<String> list;

        public BlockStmtVisitor(List<String> list) {
            this.list = list;
        }

        @Override
        public void visit(BlockStmt block, Void arg) {
            for (int i=0;i<block.getStatements().size();i++) {
                addTypeStatementInList(list, block.getStatements().get(i));
            }
        }
    }

    private static class SwitchEntryVisitor extends VoidVisitorAdapter<Void>{
        private List<String> list;

        public SwitchEntryVisitor(List<String> list){
            this.list = list;
        }

        @Override
        public void visit(SwitchEntry switchEntry, Void arg){
            NodeList<Statement> entryStatements = switchEntry.getStatements();
            if(entryStatements.isNonEmpty()){
                for(int i=0;i<entryStatements.size();i++){
                    Statement statement = entryStatements.get(i);
                    addTypeStatementInList(list, statement);
                }
            }
        }

        public void visitListSwitchEntry(NodeList<SwitchEntry> switchEntries, Void arg) {
            for (SwitchEntry switchEntry : switchEntries) {
                List<Expression> labels = switchEntry.getLabels(); 
                if(labels.isEmpty()) list.add("DefaultStmt");
                else list.add("CaseStmt");
                visit(switchEntry, arg);
            }
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
        public void visit(MethodDeclaration md, List<BlockStmt> collector){
            collector.add(md.getBody().orElse(null));
        }
    }
}
