package compiladorespj;

import java.text.CharacterIterator;
import java.util.Set;

public class SymbolRecognizer extends AFD {
    private static final Set<Character> SYMS = Set.of(
        '.',
        '(', ')',
        ',',
        ':'
    );

    @Override
    public Token evaluate(CharacterIterator code) {
        char c = code.current();
        if (SYMS.contains(c)) {
            code.next();
            return new Token("SYMBOL", String.valueOf(c));
        }
        return null;
    }
}
