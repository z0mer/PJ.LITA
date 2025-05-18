package compiladorespj;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import static compiladorespj.AST.*;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private String lastConsumedLexemeForError = "";

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return new Token("EOF", "");
    }
    
    private int getCurrentLineApprox() {
        int lines = 1;
        for (int i = 0; i < Math.min(pos, tokens.size()); i++) {
            if (tokens.get(i).getLexema().contains("\n")) {
                 lines++;
            }
        }
        return lines;
    }

    private Token consume(String expectedType) {
        Token t = peek();
        if (!t.getTipo().equals(expectedType)) {
            throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Esperado token do tipo " + expectedType +
                                       " mas encontrou " + t + " na posicao de token " + pos);
        }
        lastConsumedLexemeForError = t.getLexema();
        pos++;
        return t;
    }

    private Token consume(String expectedType, String expectedLexeme) {
        Token t = peek();
        if (!t.getTipo().equals(expectedType) || !t.getLexema().equals(expectedLexeme)) {
            throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Esperado token <" + expectedType + ", " + expectedLexeme +
                                       "> mas encontrou " + t + " na posicao de token " + pos);
        }
        lastConsumedLexemeForError = t.getLexema();
        pos++;
        return t;
    }

    public Program parseProgram() {
        consume("KEYWORD", "programma");
        String name = consume("ID").getLexema();
        Block block = parseBlock();
        consume("KEYWORD", "fineprog");
        consume("EOF");
        return new Program(name, block);
    }

    private Block parseBlock() {
        ArrayList<Statement> stmts = new ArrayList<>();
        while (!peek().getTipo().equals("EOF") &&
               !isBlockEndKeyword(peek().getLexema())) {
            if (peek().getLexema().equals("var") || peek().getLexema().equals("cost")) {
                stmts.add(parseDeclaration());
            } else if (isCommandStartKeyword(peek().getLexema()) || peek().getTipo().equals("ID")) {
                stmts.add(parseCommand());
            } else {
                throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Token inesperado no inicio do bloco ou comando: " + peek() + " na posicao de token " + pos);
            }
        }
        return new Block(stmts);
    }

    private boolean isBlockEndKeyword(String lexema) {
        return lexema.equals("fineprog") || lexema.equals("altrimenti") ||
               lexema.equals("finese") || lexema.equals("finementre") ||
               lexema.equals("fineper");
    }

    private boolean isCommandStartKeyword(String lexema) {
        return lexema.equals("leggi") || lexema.equals("scrivi") || lexema.equals("se") ||
               lexema.equals("mentre") || lexema.equals("per") || lexema.equals("ritorna") ||
               lexema.equals("interrompi");
    }

    private VarDecl parseDeclaration() {
        boolean isConst = false;
        if (peek().getLexema().equals("var")) {
            consume("KEYWORD", "var");
        } else if (peek().getLexema().equals("cost")) {
            consume("KEYWORD", "cost");
            isConst = true;
        } else {
             throw new RuntimeException("Erro de sintaxe (parseDeclaration, após '" + lastConsumedLexemeForError + "'): Esperado 'var' ou 'cost', encontrou " + peek());
        }
        String name = consume("ID").getLexema();
        consume("SYMBOL", ":");
        String type = consume("KEYWORD").getLexema();
        Expression init = null;
        if (peek().getTipo().equals("KEYWORD") && peek().getLexema().equals("assegna")) {
            consume("KEYWORD", "assegna");
            init = parseExpr();
        } else if (isConst) {
            throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Constante '" + name + "' deve ser inicializada com 'assegna expressao'.");
        }
        consume("SYMBOL", ".");
        return new VarDecl(isConst, name, type, init);
    }

    private Statement parseCommand() {
        Token current = peek();
        Statement stmt;
        switch (current.getLexema()) {
            case "leggi": stmt = parseRead(); consume("SYMBOL", "."); break;
            case "scrivi": stmt = parseWrite(); consume("SYMBOL", "."); break;
            case "se": stmt = parseIf(); break;
            case "mentre": case "per": stmt = parseRepetition(); break;
            case "ritorna": stmt = parseReturn(); consume("SYMBOL", "."); break;
            case "interrompi": consume("KEYWORD", "interrompi"); consume("SYMBOL", "."); stmt = new AST.BreakStmt(); break;
            default:
                if (current.getTipo().equals("ID")) {
                    stmt = parseAssignment();
                    consume("SYMBOL", ".");
                } else {
                    throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Comando inesperado " + current + " na posicao de token " + pos);
                }
                break;
        }
        return stmt;
    }

    private Assignment parseAssignment() {
        String name = consume("ID").getLexema();
        consume("KEYWORD", "assegna");
        Expression expr = parseExpr();
        return new Assignment(name, expr);
    }

    private Read parseRead() {
        consume("KEYWORD", "leggi");
        consume("SYMBOL", "(");
        String name = consume("ID").getLexema();
        consume("SYMBOL", ")");
        return new Read(name);
    }

    private Write parseWrite() {
        consume("KEYWORD", "scrivi");
        consume("SYMBOL", "(");
        List<Expression> expressions = new ArrayList<>();
        expressions.add(parseExpr());
        while (peek().getTipo().equals("SYMBOL") && peek().getLexema().equals(",")) {
            consume("SYMBOL", ",");
            expressions.add(parseExpr());
        }
        consume("SYMBOL", ")");
        return new Write(expressions);
    }

    private IfStmt parseIf() {
        consume("KEYWORD", "se");
        consume("SYMBOL", "(");
        Condition cond = parseCond();
        consume("SYMBOL", ")");
        consume("KEYWORD", "allora");
        Block thenB = parseBlock();
        Block elseB = null;
        if (peek().getTipo().equals("KEYWORD") && peek().getLexema().equals("altrimenti")) {
            consume("KEYWORD", "altrimenti");
            elseB = parseBlock();
        }
        consume("KEYWORD", "finese");
        return new IfStmt(cond, thenB, elseB);
    }
    
    private Statement parseRepetition() {
        if (peek().getLexema().equals("mentre")) {
            return parseWhile();
        } else if (peek().getLexema().equals("per")) {
            return parseFor();
        } else {
            throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Esperado 'mentre' ou 'per', encontrou " + peek());
        }
    }

    private WhileStmt parseWhile() {
        consume("KEYWORD", "mentre");
        consume("SYMBOL", "(");
        Condition cond = parseCond();
        consume("SYMBOL", ")");
        consume("KEYWORD", "fai");
        Block b = parseBlock();
        consume("KEYWORD", "finementre");
        return new WhileStmt(cond, b);
    }

    private ForStmt parseFor() {
        consume("KEYWORD", "per");
        consume("SYMBOL", "(");
        Assignment init = parseAssignment(); consume("SYMBOL", ".");
        Condition cond = parseCond();        consume("SYMBOL", ".");
        Assignment update = parseAssignment();
        consume("SYMBOL", ")");
        consume("KEYWORD", "fai");
        Block b = parseBlock();
        consume("KEYWORD", "fineper");
        return new ForStmt(init, cond, update, b);
    }

    private ReturnStmt parseReturn() {
        consume("KEYWORD", "ritorna");
        Expression e = parseExpr();
        return new ReturnStmt(e);
    }

    private Condition parseCond() {
        Expression expr = parseExpr();

        Token opToken = peek();
        Set<String> relationalOps = Set.of("uguale", "diverso", "minore", "maggiore", "minoreuguale", "maggioreuguale");
        if (opToken.getTipo().equals("OPERATOR") && relationalOps.contains(opToken.getLexema())) {
            consume("OPERATOR");
            Expression rightExpr = parseExpr();
            return new Condition(expr, opToken.getLexema(), rightExpr);
        } else {
            return new Condition(expr);
        }
    }

    private Expression parseExpr() {
        Expression left = parseTerm();
        Set<String> arithmeticOps = Set.of("piu", "meno", "moltiplica", "diviso", "%");
        while (peek().getTipo().equals("OPERATOR") && arithmeticOps.contains(peek().getLexema())) {
            String op = consume("OPERATOR").getLexema();
            Expression right = parseTerm();
            left = new BinaryExpr(left, op, right);
        }
        return left;
    }

    private Expression parseTerm() {
        Token t = peek();
        switch (t.getTipo()) {
            case "NUM_INT":
                pos++;
                try { return new Literal(Integer.parseInt(t.getLexema())); }
                catch (NumberFormatException e) { throw new RuntimeException("Erro ao converter NUM_INT '" + t.getLexema() + "' na pos " + (pos-1)); }
            case "NUM_DEC":
                pos++;
                String lexemaOriginal = t.getLexema();
                String lexemaParaParse = lexemaOriginal.replace(',', '.');
                try { return new Literal(Double.parseDouble(lexemaParaParse)); }
                catch (NumberFormatException e) { throw new RuntimeException("Erro ao converter NUM_DEC '" + lexemaOriginal + "' para double na pos " + (pos-1));}
            case "STRING":
                pos++; return new Literal(t.getLexema());
            case "ID":
                pos++; return new Variable(t.getLexema());
            case "KEYWORD":
                if (t.getLexema().equals("vero")) { pos++; return new Literal(true); }
                else if (t.getLexema().equals("falso")) { pos++; return new Literal(false); }
                throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Keyword '" + t.getLexema() + "' inesperada como termo na posição de token " + pos);
            case "SYMBOL":
                if (t.getLexema().equals("(")) {
                    consume("SYMBOL", "(");
                    Expression e = parseExpr();
                    consume("SYMBOL", ")");
                    return e;
                }
                throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Simbolo '" + t.getLexema() + "' inesperado como termo na posicao de token " + pos);
            default:
                throw new RuntimeException("Erro de sintaxe (linha ~" + getCurrentLineApprox() + ", apos '" + lastConsumedLexemeForError + "'): Termo inesperado " + t + " (tipo: " + t.getTipo() + ")" + " na posicao de token " + pos);
        }
    }
}
