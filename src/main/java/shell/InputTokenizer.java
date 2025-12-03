package shell;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class InputTokenizer {
    private static final Pattern redirectionTokenPattern = Pattern.compile("^[0-9]*(>|>>)$");

    /**
     * @throws IllegalArgumentException if there is syntax error in provided command arguments
     */
    public static List<Token> tokenize(String command) throws IllegalArgumentException {
        List<Token> ls = new ArrayList<>();
        if (command.isEmpty()) return ls;
        boolean singleQuoteOpen = false;
        boolean doubleQuoteOpen = false;
        boolean escaping = false;
        int i = 0;
        TokenType tokenType = TokenType.LITERAL;
        StringBuilder sb = new StringBuilder();
        while (i < command.length()) {
            char currChar = command.charAt(i++);
            if (escaping) {
                escaping = false;
                sb.append(currChar);
            } else if (currChar == '\'' && !doubleQuoteOpen) {
                singleQuoteOpen = !singleQuoteOpen;
            } else if (currChar == '\"' && !singleQuoteOpen) {
                doubleQuoteOpen = !doubleQuoteOpen;
            } else if (currChar == ' ' && !(singleQuoteOpen || doubleQuoteOpen)) {
                if (!sb.isEmpty()) {
                    ls.add(new Token(tokenType, sb.toString()));
                }
                sb = new StringBuilder();
                tokenType = TokenType.LITERAL;
            } else if (currChar == '>' && !(doubleQuoteOpen || singleQuoteOpen)) { //todo validate syntax
                tokenType = TokenType.REDIRECT;
                sb.append(currChar);
            } else if (currChar == '\\' && !singleQuoteOpen && !doubleQuoteOpen) { // outside of single and double quotes (escape)
                escaping = true;
            } else if (currChar == '\\' && doubleQuoteOpen) { //inside of double quotes (conditional escape)
                if (i >= command.length()) {
                    throw new IllegalArgumentException("Expected the escaped character at the end");
                }
                currChar = switch (command.charAt(i)) {
                    case '"', '\\' -> command.charAt(i++);
                    default -> currChar;
                };
                sb.append(currChar);
            } else {
                sb.append(currChar);
            }

        }
        if (!sb.isEmpty()) {
            ls.add(new Token(tokenType, sb.toString()));
        }
        validateTokens(ls);
        return ls;
    }


    private static void validateTokens(List<Token> tokens) throws IllegalArgumentException {
        for (int i = 0; i < tokens.size(); i++) {
            Token curr = tokens.get(i);
            if (curr.type == TokenType.REDIRECT) {
                if (!redirectionTokenPattern.matcher(curr.value).matches())
                    throw new IllegalArgumentException("Invalid syntax: " + curr.value);
                if (i + 1 >= tokens.size())
                    throw new IllegalArgumentException("Expected file name after: " + curr.value);
                Token next = tokens.get(i + 1);
                if(next.type != TokenType.LITERAL)
                    throw new IllegalArgumentException("Not a valid file name: " + next.value);
                if(i + 1 != tokens.size() - 1)
                    throw new IllegalArgumentException("Invalid syntax."); //file name should be last in the tokens.
            }
        }
    }

}
