package compiladorespj;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static compiladorespj.AST.*;

public class SemanticAnalyzer {
    private final Map<String,String> symtab = new HashMap<>();

    public void analyze(Program p) {
        if (p == null || p.block == null) {
            error("Programa ou bloco principal nulo na AST.");
            return;
        }
        visitBlock(p.block);

        System.out.println("--- Tabela de Simbolos (Apos Analise Semantica) ---");
        if (symtab.isEmpty()) {
            System.out.println("Tabela de simbolos vazia.");
        } else {
            for (Map.Entry<String, String> entry : symtab.entrySet()) {
                System.out.println("ID: " + entry.getKey() + ", Tipo: " + entry.getValue());
            }
        }
        System.out.println("----------------------------------------------------");
    }

    private void visitBlock(Block b) {
        if (b == null || b.statements == null) return;
        for (Statement st : b.statements) {
            visitStmt(st);
        }
    }

    private void visitStmt(Statement st) {
        if (st == null) return;
        String stmtClassName = st.getClass().getSimpleName();
        switch (stmtClassName) {
            case "VarDecl":    checkVarDecl((VarDecl)st);   break;
            case "Assignment": checkAssign((Assignment)st); break;
            case "Read":       checkRead((Read)st);         break;
            case "Write":      checkWrite((Write)st);       break;
            case "IfStmt":     checkIf((IfStmt)st);         break;
            case "WhileStmt":  checkWhile((WhileStmt)st);   break;
            case "ForStmt":    checkFor((ForStmt)st);       break;
            case "ReturnStmt": checkReturn((ReturnStmt)st); break;
            case "BreakStmt":  checkBreak((BreakStmt)st);   break;
            default:
                error("Analise Sematica: Stmt nao suportado: " + stmtClassName);
        }
    }

    private void checkVarDecl(VarDecl d) {
        if (symtab.containsKey(d.name)) {
            error("Variavel '" + d.name + "' ja declarada.");
        }
        symtab.put(d.name, d.type);
        if (d.init != null) {
            String tExpr = typeOf(d.init);
            if (!canAssign(d.type, tExpr)) {
                error("Inicializador da variavel '" + d.name + "' (tipo " + d.type +
                      ") é incompativel com o tipo da expressao '" + tExpr + "'.");
            }
        }
    }
    
    private boolean canAssign(String varType, String exprType) {
        if (varType.equals(exprType)) return true;
        if (varType.equals("decimale") && exprType.equals("intero")) return true;
        return false;
    }

    private void checkAssign(Assignment a) {
        if (!symtab.containsKey(a.name)) {
            error("Variael '" + a.name + "' nao declarada antes de ser usada em atribuicao.");
        }
        String tVar  = symtab.get(a.name);
        String tExpr = typeOf(a.expr);
        if (!canAssign(tVar, tExpr)) {
            error("Atribuicao invalida para variavel '" + a.name + "': tipo da variavel e '" + tVar +
                  "' mas o tipo da expressao e '" + tExpr + "'.");
        }
    }

    private void checkRead(Read r) {
        if (!symtab.containsKey(r.name)) {
            error("Variavel '" + r.name + "' usada em 'leggi' nao foi declarada.");
        }
    }

    private void checkWrite(Write w) {
        if (w.expressions == null || w.expressions.isEmpty()) return;
        for (Expression expr : w.expressions) {
            typeOf(expr);
        }
    }

    private void checkIf(IfStmt i) {
        String condType = typeOf(i.cond);
        if (!condType.equals("booleano")) {
            error("Condicao do comando 'se' deve ser do tipo 'booleano', mas foi '" + condType + "'.");
        }
        visitBlock(i.thenBlock);
        if (i.elseBlock != null) {
            visitBlock(i.elseBlock);
        }
    }

    private void checkWhile(WhileStmt w) {
        String condType = typeOf(w.cond);
        if (!condType.equals("booleano")) {
            error("Condiçao do comando 'mentre' deve ser do tipo 'booleano', mas foi '" + condType + "'.");
        }
        visitBlock(w.block);
    }

    private void checkFor(ForStmt f) {
        checkAssign(f.init);

        String condType = typeOf(f.cond);
        if (!condType.equals("booleano")) {
            error("Condicao do comando 'per' deve ser do tipo 'booleano', mas foi '" + condType + "'.");
        }
        checkAssign(f.update);
        visitBlock(f.block);
    }

    private void checkReturn(ReturnStmt r) {
        typeOf(r.expr);
    }

    private void checkBreak(BreakStmt b) {
    }

    private String typeOf(Expression e) {
        if (e == null) error("Tentativa de obter tipo de expressao nula.");
        String exprClassName = e.getClass().getSimpleName();
        switch (exprClassName) {
            case "Literal":
                Object v = ((Literal)e).value;
                if (v instanceof Integer) return "intero";
                if (v instanceof Double)  return "decimale";
                if (v instanceof Boolean) return "booleano";
                if (v instanceof String)  return "testo";
                error("Tipo de literal desconhecido: " + v.getClass().getName());
                return "desconhecido";
            case "Variable":
                String name = ((Variable)e).name;
                if (!symtab.containsKey(name)) {
                    error("Uso da variavel nao declarada: '" + name + "'.");
                }
                return symtab.get(name);
            case "BinaryExpr":
                BinaryExpr b = (BinaryExpr)e;
                String lt = typeOf(b.left);
                String rt = typeOf(b.right);
                String op = b.op;

                if (Set.of("piu", "meno", "moltiplica", "diviso", "%").contains(op)) {
                    if ((lt.equals("intero") || lt.equals("decimale")) &&
                        (rt.equals("intero") || rt.equals("decimale"))) {
                        if (op.equals("%") && (lt.equals("decimale") || rt.equals("decimale"))) {
                            error("Operador '%' nao pode ser usado com tipos 'decimale'.");
                        }
                        return (lt.equals("decimale") || rt.equals("decimale")) ? "decimale" : "intero";
                    } else if (lt.equals("testo") && op.equals("piu") && rt.equals("testo")) {
                        return "testo";
                    } else {
                        error("Operador aritmetico '" + op + "' aplicado a tipos incompatieis: " + lt + " e " + rt + ".");
                    }
                }
                error("Operador binario desconhecido ou mal utilizado em expressao: '" + op + "'.");
                return "desconhecido";
            default:
                error("Tipo de expressao desconhecido para anaise semantica: " + exprClassName);
                return "desconhecido";
        }
    }

    private String typeOf(Condition c) {
        if (c.singleBooleanExpression != null) {
            String exprType = typeOf(c.singleBooleanExpression);
            if (!exprType.equals("booleano")) {
                error("Condicao esperava uma expressao do tipo 'booleano', mas obteve '" + exprType + "'.");
            }
            return "booleano";
        } else {
            String lt = typeOf(c.left);
            String rt = typeOf(c.right);
            boolean numericComparison = (lt.equals("intero") || lt.equals("decimale")) &&
                                        (rt.equals("intero") || rt.equals("decimale"));
            boolean stringComparison = lt.equals("testo") && rt.equals("testo") && 
                                       (c.op.equals("uguale") || c.op.equals("diverso"));
            boolean booleanComparison = lt.equals("booleano") && rt.equals("booleano") && 
                                        (c.op.equals("uguale") || c.op.equals("diverso"));

            if (numericComparison) {
            } else if (stringComparison || booleanComparison) {
            }
             else if (!lt.equals(rt)) {
                 error("Comparacao '" + c.op + "' entre tipos incompativeis: " + lt + " e " + rt + ".");
            }
            if ((lt.equals("testo") || lt.equals("booleano")) && 
                !(c.op.equals("uguale") || c.op.equals("diverso"))) {
                error("Operador '" + c.op + "' nao e valido para tipos '" + lt + "'. Use 'uguale' ou 'diverso'.");
            }
            return "booleano";
        }
    }

    private void error(String msg) {
        throw new RuntimeException("Erro Semantico: " + msg);
    }
}
