package compiladorespj;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final List<AFD> afds = List.of(
        new MathOperator(),
        new KeywordRecognizer(),
        new IdentifierRecognizer(),
        new NumberRecognizer(),
        new StringRecognizer(),
        new SymbolRecognizer()
    );
    
    private final CharacterIterator code;
    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String input) {
        this.code = new StringCharacterIterator(input);
    }

    private void skipWS() {
        while (code.current() != CharacterIterator.DONE && Character.isWhitespace(code.current())) {
            code.next();
        }
    }

    public List<Token> tokenize() {
        while (code.current() != CharacterIterator.DONE) {
            skipWS();
            
            if (code.current() == CharacterIterator.DONE) break;
            
            boolean matched = false;
            for (AFD afd : afds) {
                Token t = afd.evaluate(code);
                
                if (t != null) {
                    tokens.add(t);
                    matched = true;
                    break;
                }
            }
            
            if (!matched) {
                throw new RuntimeException(
                  "Erro Lexico: Simbolo '" + code.current() +
                  "' nao reconhecido na posicao " + code.getIndex()
                );
            }
        }
        tokens.add(new Token("EOF", ""));
        return tokens;
    }
}
