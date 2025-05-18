package compiladorespj;

import java.text.CharacterIterator;

public class IdentifierRecognizer extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        int start = code.getIndex();
        if (!Character.isLetter(code.current())) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(code.current()) || code.current() == '_') {
            sb.append(code.current());
            code.next();
        }
        return new Token("ID", sb.toString());
    }
}
