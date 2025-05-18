package compiladorespj;

import java.text.CharacterIterator;
import java.util.Set;

public class KeywordRecognizer extends AFD {
    private static final Set<String> KEYWORDS = Set.of(
        "programma", "fineprog",
        "var", "cost",
        "intero", "decimale", "testo", "booleano",
        "leggi", "scrivi",
        "se", "allora", "altrimenti", "finese",
        "mentre", "fai", "finementre",
        "per", "fineper",
        "ritorna",
        "vero", "falso",
        "assegna",
        "interrompi"
        ,"e", "non"
    );

    @Override
    public Token evaluate(CharacterIterator code) {
        int startPos = code.getIndex();
        StringBuilder sb = new StringBuilder();

        while (Character.isLetter(code.current())) {
            sb.append(code.current());
            code.next();
        }

        String firstWord = sb.toString();
        if (firstWord.isEmpty()) {
            code.setIndex(startPos);
            return null;
        }

        if (firstWord.equals("fine")) {
            StringBuilder secondWordSb = new StringBuilder();
            int tempPos = code.getIndex();
            while(Character.isWhitespace(code.current())) {
                code.next();
            }
            boolean hasSpace = code.getIndex() > tempPos;

            if (hasSpace && Character.isLetter(code.current())) {
                while (Character.isLetter(code.current())) {
                    secondWordSb.append(code.current());
                    code.next();
                }
                String secondWord = secondWordSb.toString();
                String combinedKeyword = firstWord + secondWord;

                if (KEYWORDS.contains(combinedKeyword)) {
                    return new Token("KEYWORD", combinedKeyword);
                } else {
                    code.setIndex(startPos);
                    return null;
                }
            } else {
                code.setIndex(startPos);
                return null;
            }
        }

        if (KEYWORDS.contains(firstWord)) {
            return new Token("KEYWORD", firstWord);
        }

        code.setIndex(startPos);
        return null;
    }
}
