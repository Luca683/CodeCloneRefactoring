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
        BlockStmt blockStmt = null;
        list.add(statementType);
        if(st.isForStmt()) blockStmt = st.asForStmt().getBody().asBlockStmt();
        else if(st.isWhileStmt()) blockStmt = st.asWhileStmt().getBody().asBlockStmt();
        else if(st.isForEachStmt()) blockStmt = st.asForEachStmt().getBody().asBlockStmt();
        else if(st.isDoStmt()) blockStmt = st.asDoStmt().getBody().asBlockStmt();
        else if(st.isIfStmt()) blockStmt = st.asIfStmt().getThenStmt().asBlockStmt();
        else if(st.isTryStmt()) blockStmt = st.asTryStmt().getTryBlock();
        if(blockStmt != null)
            //Dato un nuovo blocco, creo una nuova lista contenente la tipologia di tutti i suoi statement e la concateno a quella principale
            list.addAll(createListOfTypeStatement(blockStmt));
        list.add("End"+statementType);

        if(st.isIfStmt()){
            //else
            if(st.asIfStmt().hasElseBranch()) {
                list.add("ElseStmt");
                Statement elseStmt = st.asIfStmt().getElseStmt().get();
                if(elseStmt.isBlockStmt()) {
                    list.addAll(createListOfTypeStatement(elseStmt.asBlockStmt()));
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
                list.addAll(createListOfTypeStatement(blockStmt));
                list.add("EndCatchClause");
            }
            //finally
            blockStmt = st.asTryStmt().getFinallyBlock().orElse(null);
            if(blockStmt != null){
                list.add("FinallyStmt");
                list.addAll(createListOfTypeStatement(blockStmt));
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
            list.add("EndSwitchStmt");
        }
        else if(st.isForStmt() || st.isWhileStmt() || st.isForEachStmt() || st.isDoStmt() || st.isIfStmt() || st.isTryStmt()){
            scanComplexStatement(st, list);
        }
        else list.add(st.getClass().getSimpleName()); //Funziona per returnStmt o breakStmt
    }
    
    //Dato un blocco di statement, crea una lista di stringhe contenente per ogni statement la sua tipologia 
    public static List<String> createListOfTypeStatement(BlockStmt block){
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

    private static class SwitchEntryVisitor extends VoidVisitorAdapter<Void>{
        private List<String> list;

        public SwitchEntryVisitor(List<String> list){
            this.list = list;
        }

        @Override
        public void visit(SwitchEntry switchEntry, Void arg){
            //Data una entry (case o default) se non vuota la ispeziono
            NodeList<Statement> entryStatements = switchEntry.getStatements();
            if(entryStatements.isNonEmpty()){
                for(int i=0;i<entryStatements.size();i++){
                    Statement statement = entryStatements.get(i);
                    addTypeStatementInList(list, statement);
                }
            }
        }

        public void visitListSwitchEntry(NodeList<SwitchEntry> switchEntries, Void arg) {
            //Per ogni entry di uno switch controllo se è un case o un default
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
