package compiladorespj;

import java.text.CharacterIterator;

public class StringRecognizer extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() != '"') return null;

        StringBuilder sb = new StringBuilder();
        code.next();

        while (code.current() != CharacterIterator.DONE && code.current() != '"') {
            if (code.current() == '\\') {
                sb.append(code.current());
                code.next();
                if (code.current() != CharacterIterator.DONE) {
                    sb.append(code.current());
                    code.next();
                }
            } else {
                sb.append(code.current());
                code.next();
            }
        }

        if (code.current() == '"') {
            code.next();
            return new Token("STRING", sb.toString());
        }
        throw new RuntimeException("Erro Lexico: Literal de string nao fechado corretamente na posicao " + code.getIndex());
    }
}
