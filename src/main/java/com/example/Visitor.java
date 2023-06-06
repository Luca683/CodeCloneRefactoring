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
            list.add("WhileStmt"); //E' corretto iserire "WhileStmt" come delimitatore? Oppure meglio "EndDo"? 
        }
        //Da rivedere la parte sull'ElseBranch
        else if(st.isIfStmt()){
            list.add("IfStmt");
            BlockStmt blockStmt = st.asIfStmt().getThenStmt().asBlockStmt();
            BlockStmtVisitor visitor = new BlockStmtVisitor(list);
            visitor.visit(blockStmt, null);
            list.add("EndIf");
            if(st.asIfStmt().hasElseBlock()){ //Se l'else è presente ma è caratterizzato da un BlockStmt
                list.add("ElseStmt");
                blockStmt = st.asIfStmt().getElseStmt().get().asBlockStmt();
                visitor.visit(blockStmt, null);
                list.add("EndElse");
            }
            else if(st.asIfStmt().hasElseBranch()){ //Se l'else è presente e ha un singolo statement
                list.add("ElseStmt");
                addTypeStatementInList(list, st.asIfStmt().getElseStmt().get());
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
        //try catch
        else if(st.isTryStmt()){
            list.add("TryStmt");
            BlockStmt blockStmt = st.asTryStmt().getTryBlock();
            BlockStmtVisitor visitor = new BlockStmtVisitor(list);
            visitor.visit(blockStmt, null);
            list.add("EndTryStmt");
            NodeList<CatchClause> catchBlocks = st.asTryStmt().getCatchClauses();
            for(int i=0;i<catchBlocks.size();i++){
                list.add("CatchClause");
                blockStmt = catchBlocks.get(i).getBody().asBlockStmt();
                visitor.visit(blockStmt, null);
                list.add("EndCatchClause");
            }
        }
        else list.add(st.getClass().getSimpleName()); //Da togliere appena avrò considerato tutti i possibili casi
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
            if (entryStatements.isNonEmpty()){
                for(int i=0;i<entryStatements.size();i++){
                    Statement statement = entryStatements.get(i);
                    addTypeStatementInList(list, statement);
                }
            }
        }

        public void visitListSwitchEntry(NodeList<SwitchEntry> switchEntries, Void arg) {
            for (SwitchEntry switchEntry : switchEntries) {
                List<Expression> labels = switchEntry.getLabels();
                /*for (Expression label : labels) {
                    String labelText = label.toString();
                    list.add("Case: " + labelText);
                }*/ //Usando questo codice insieme a "Case" viene stampata anche la label (quindi nel nostro caso Case: 1, Case: 2 ecc..) 
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
        public void visit(BlockStmt bs, List<BlockStmt> collector){
            collector.add(bs);
        }
    }

}
