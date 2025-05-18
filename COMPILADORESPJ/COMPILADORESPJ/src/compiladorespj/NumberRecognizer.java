package compiladorespj;

import java.text.CharacterIterator;

public class NumberRecognizer extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (!Character.isDigit(code.current())) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        while (Character.isDigit(code.current())) {
            sb.append(code.current());
            code.next();
        }

        char currentChar = code.current();
        int potentialSeparatorIndex = code.getIndex();

        if (currentChar == ',') {
            code.next();
            if (Character.isDigit(code.current())) {
                sb.append(currentChar);
                do {
                    sb.append(code.current());
                    code.next();
                } while (Character.isDigit(code.current()));
                return new Token("NUM_DEC", sb.toString());
            } else {
                code.setIndex(potentialSeparatorIndex);
                return new Token("NUM_INT", sb.toString());
            }
        }
        return new Token("NUM_INT", sb.toString());
    }
}
