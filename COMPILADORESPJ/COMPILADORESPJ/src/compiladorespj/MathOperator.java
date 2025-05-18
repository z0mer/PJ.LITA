package compiladorespj;

import java.text.CharacterIterator;
import java.util.Set;

public class MathOperator extends AFD {
    private static final Set<String> OPS_EXTENSO = Set.of(
        "piu", "meno", "moltiplica", "diviso",
        "uguale", "diverso", "minore", "maggiore",
        "minoreuguale", "maggioreuguale"
    );

    private static final Set<Character> OPS_SIMBOLICOS = Set.of(
        '%'
    );

    @Override
    public Token evaluate(CharacterIterator code) {
        int start = code.getIndex();
        char currentChar = code.current();

        if (OPS_SIMBOLICOS.contains(currentChar)) {
            code.next();
            return new Token("OPERATOR", String.valueOf(currentChar));
        }

        StringBuilder sb = new StringBuilder();
        while (Character.isLetter(code.current())) {
            sb.append(code.current());
            code.next();
        }
        
        String potentialOperatorWord = sb.toString();
        if (!potentialOperatorWord.isEmpty() && OPS_EXTENSO.contains(potentialOperatorWord)) {
            return new Token("OPERATOR", potentialOperatorWord);
        }

        code.setIndex(start);
        return null;
    }
}
