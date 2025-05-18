package compiladorespj;

import java.util.List;

public class AST {

    public static class Program {
        public final String name;
        public final Block block;

        public Program(String name, Block block) {
            this.name = name;
            this.block = block;
        }
        @Override public String toString() { return "Program(" + name + ")"; }
    }

    public static class Block {
        public final List<Statement> statements;

        public Block(List<Statement> statements) {
            this.statements = statements;
        }
        @Override public String toString() { return "Block(num_stmts=" + (statements != null ? statements.size() : 0) + ")"; }
    }

    public interface Statement { }

    public static class VarDecl implements Statement {
        public final boolean isConst;
        public final String name;
        public final String type;
        public final Expression init;

        public VarDecl(boolean isConst, String name, String type, Expression init) {
            this.isConst = isConst;
            this.name = name;
            this.type = type;
            this.init = init;
        }
        @Override public String toString() { return (isConst?"ConstDecl(":"VarDecl(") + name + ":" + type + (init != null ? "=" + init : "") + ")"; }
    }

    public static class Assignment implements Statement {
        public final String name;
        public final Expression expr;

        public Assignment(String name, Expression expr) {
            this.name = name;
            this.expr = expr;
        }
        @Override public String toString() { return "Assignment(" + name + "=" + expr + ")"; }
    }

    public static class Read implements Statement {
        public final String name;

        public Read(String name) { this.name = name; }
        @Override public String toString() { return "Read(" + name + ")"; }
    }

    public static class Write implements Statement {
        public final List<Expression> expressions;

        public Write(List<Expression> expressions) { this.expressions = expressions; }
        @Override public String toString() { return "Write(" + expressions + ")"; }
    }

    public static class IfStmt implements Statement {
        public final Condition cond;
        public final Block thenBlock;
        public final Block elseBlock;

        public IfStmt(Condition cond, Block thenBlock, Block elseBlock) {
            this.cond = cond;
            this.thenBlock = thenBlock;
            this.elseBlock = elseBlock;
        }
        @Override public String toString() { return "IfStmt(Cond:" + cond + ", HasElse:"+(elseBlock != null)+")"; }
    }

    public static class WhileStmt implements Statement {
        public final Condition cond;
        public final Block block;

        public WhileStmt(Condition cond, Block block) {
            this.cond = cond;
            this.block = block;
        }
        @Override public String toString() { return "WhileStmt(" + cond + ")"; }
    }

    public static class ForStmt implements Statement {
        public final Assignment init;
        public final Condition cond;
        public final Assignment update;
        public final Block block;

        public ForStmt(Assignment init, Condition cond, Assignment update, Block block) {
            this.init = init;
            this.cond = cond;
            this.update = update;
            this.block = block;
        }
        @Override public String toString() { return "ForStmt(" + init + ";" + cond + ";" + update + ")"; }
    }

    public static class ReturnStmt implements Statement {
        public final Expression expr;

        public ReturnStmt(Expression expr) { this.expr = expr; }
        @Override public String toString() { return "ReturnStmt(" + expr + ")"; }
    }
    
    public static class BreakStmt implements Statement {
         public BreakStmt() {}
         @Override public String toString() { return "BreakStmt"; }
    }

    public interface Expression { }

    public static class Literal implements Expression {
        public final Object value;

        public Literal(Object value) { this.value = value; }
        @Override public String toString() {
            if (value instanceof String) return "Literal(\"" + value + "\")";
            return "Literal(" + value + ")";
        }
    }

    public static class Variable implements Expression {
        public final String name;

        public Variable(String name) { this.name = name; }
        @Override public String toString() { return "Variable(" + name + ")"; }
    }

    public static class BinaryExpr implements Expression {
        public final Expression left;
        public final String op;
        public final Expression right;

        public BinaryExpr(Expression left, String op, Expression right) {
            this.left = left; this.op = op; this.right = right;
        }
        @Override public String toString() { return "BinaryExpr(" + left + " " + op + " " + right + ")"; }
    }

    public static class Condition {
        public final Expression left;
        public final String op;
        public final Expression right;
        public final Expression singleBooleanExpression;

        public Condition(Expression left, String op, Expression right) {
            this.left = left;
            this.op = op;
            this.right = right;
            this.singleBooleanExpression = null;
        }

        public Condition(Expression singleBooleanExpression) {
            this.singleBooleanExpression = singleBooleanExpression;
            this.left = null;
            this.op = null;
            this.right = null;
        }
        @Override public String toString() {
            if (singleBooleanExpression != null) return "Condition(" + singleBooleanExpression + ")";
            return "Condition(" + left + " " + op + " " + right + ")";
        }
    }
}
