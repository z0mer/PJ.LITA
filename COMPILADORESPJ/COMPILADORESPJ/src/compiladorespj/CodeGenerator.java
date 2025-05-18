package compiladorespj;

import static compiladorespj.AST.*;
import java.util.List;

public class CodeGenerator {

    public String generate(Program p) {
        if (p == null) {
            throw new IllegalArgumentException("No do programa (Program) nao pode ser nulo para geracao de codigo.");
        }
        StringBuilder sb = new StringBuilder();

        sb.append("#include <iostream>\n");
        sb.append("#include <string>\n");
        sb.append("#include <vector>\n");
        sb.append("#include <stdexcept>\n");
        sb.append("\n");
        sb.append("using namespace std;\n\n");

        sb.append("int main() {\n");
        if (p.block != null) {
            genBlock(p.block, sb, "    ");
        }
        sb.append("    return 0;\n");
        sb.append("}\n");

        return sb.toString();
    }

    private void genBlock(Block b, StringBuilder sb, String indent) {
        if (b == null || b.statements == null) {
            return;
        }
        for (Statement st : b.statements) {
            genStmt(st, sb, indent);
        }
    }

    private void genStmt(Statement st, StringBuilder sb, String indent) {
        if (st == null) return;

        String stmtClassName = st.getClass().getSimpleName();

        switch (stmtClassName) {
            case "VarDecl":
                VarDecl d = (VarDecl) st;
                sb.append(indent);
                if (d.isConst) sb.append("const ");
                sb.append(mapType(d.type)).append(" ").append(d.name);
                if (d.init != null) sb.append(" = ").append(genExpr(d.init));
                sb.append(";\n");
                break;

            case "Assignment":
                Assignment a = (Assignment) st;
                sb.append(indent).append(a.name).append(" = ").append(genExpr(a.expr)).append(";\n");
                break;

            case "Read":
                Read r = (Read) st;
                sb.append(indent).append("cin >> ").append(r.name).append(";\n");
                break;

            case "Write":
                Write w = (Write) st;
                sb.append(indent).append("cout");
                if (w.expressions != null) {
                    for (Expression expr : w.expressions) {
                        sb.append(" << ").append(genExpr(expr));
                    }
                }
                sb.append(" << endl;\n");
                break;

            case "IfStmt":
                IfStmt i = (IfStmt) st;
                sb.append(indent).append("if (").append(genCond(i.cond)).append(") {\n");
                genBlock(i.thenBlock, sb, indent + "    ");
                if (i.elseBlock != null) {
                    sb.append(indent).append("} else {\n");
                    genBlock(i.elseBlock, sb, indent + "    ");
                }
                sb.append(indent).append("}\n");
                break;

            case "WhileStmt":
                WhileStmt ws = (WhileStmt) st;
                sb.append(indent).append("while (").append(genCond(ws.cond)).append(") {\n");
                genBlock(ws.block, sb, indent + "    ");
                sb.append(indent).append("}\n");
                break;

            case "ForStmt":
                ForStmt f = (ForStmt) st;
                sb.append(indent).append("for (")
                  .append(f.init.name).append(" = ").append(genExpr(f.init.expr)).append("; ")
                  .append(genCond(f.cond)).append("; ")
                  .append(f.update.name).append(" = ").append(genExpr(f.update.expr))
                  .append(") {\n");
                genBlock(f.block, sb, indent + "    ");
                sb.append(indent).append("}\n");
                break;

            case "ReturnStmt":
                ReturnStmt rs = (ReturnStmt) st;
                sb.append(indent).append("return ").append(genExpr(rs.expr)).append(";\n");
                break;

            case "BreakStmt":
                sb.append(indent).append("break;\n");
                break;

            default:
                throw new RuntimeException("CodeGenerator: Geracao de Stmt nao suportado para: " + stmtClassName);
        }
    }

    private String genExpr(Expression e) {
        if (e == null) {
            throw new IllegalArgumentException("Expressão nao pode ser nula para geracao de codigo.");
        }
        String exprClassName = e.getClass().getSimpleName();
        switch (exprClassName) {
            case "Literal":
                Object v = ((Literal) e).value;
                if (v instanceof String) {
                    return "\"" + v.toString().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
                }
                if (v instanceof Boolean) return ((Boolean) v) ? "true" : "false";
                return v.toString();
            case "Variable":
                return ((Variable) e).name;
            case "BinaryExpr":
                BinaryExpr b = (BinaryExpr) e;
                return "(" + genExpr(b.left) + " " + mapOp(b.op) + " " + genExpr(b.right) + ")";
            default:
                throw new RuntimeException("CodeGenerator: Geracao de Expr nao suportada para: " + exprClassName);
        }
    }

    private String genCond(Condition c) {
        if (c == null) {
            throw new IllegalArgumentException("Condicao nao pode ser nula para geracao de codigo.");
        }
        if (c.singleBooleanExpression != null) {
            return genExpr(c.singleBooleanExpression);
        } else {
            if (c.op == null) {
                throw new IllegalStateException("CodeGenerator: Operador de condicao (c.op) nulo.");
            }
            return genExpr(c.left) + " " + mapOp(c.op) + " " + genExpr(c.right);
        }
    }

    private String mapType(String sourceType) {
        if (sourceType == null) {
             throw new IllegalArgumentException("Tipo de origem nao pode ser nulo em mapType.");
        }
        return switch (sourceType) {
            case "intero"   -> "int";
            case "decimale" -> "double";
            case "booleano" -> "bool";
            case "testo"    -> "string";
            default         -> throw new RuntimeException("CodeGenerator: Tipo desconhecido para mapeamento C++: '" + sourceType + "'");
        };
    }

    private String mapOp(String sourceOp) {
        if (sourceOp == null) {
            throw new IllegalArgumentException("Operador nao pode ser nulo em mapOp.");
        }
        return switch (sourceOp) {
            case "piu"             -> "+";
            case "meno"            -> "-";
            case "moltiplica"      -> "*";
            case "diviso"          -> "/";
            case "%"               -> "%";
            case "uguale"          -> "==";
            case "diverso"         -> "!=";
            case "minore"          -> "<";
            case "maggiore"        -> ">";
            case "minoreuguale"    -> "<=";
            case "maggioreuguale"  -> ">=";
            default                -> throw new RuntimeException("CodeGenerator: Operador desconhecido para mapeamento C++: '" + sourceOp + "'");
        };
    }
}
