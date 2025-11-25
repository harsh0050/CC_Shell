import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Command {
    private String[] argv;
    private boolean redirectStandardOutput, redirectStandardError, redirectStandardInput;
    private BufferedWriterWrapper outputStreamWriter;
    private BufferedWriterWrapper errorStreamWriter;
    private BufferedReader inputStreamReader;

    public Command(String command) throws IllegalArgumentException, IOException{
        this.outputStreamWriter = BufferedWriterWrapper.fromPrintStream(System.out);
        this.errorStreamWriter = BufferedWriterWrapper.fromPrintStream(System.err);
        this.inputStreamReader = new BufferedReader(new InputStreamReader(System.in));
        List<Token> tokens = tokenize(command);
        initFromTokens(tokens);
        redirectStandardError = redirectStandardInput = redirectStandardOutput = false;
    }

    private void initFromTokens(List<Token> tokens) throws IllegalArgumentException, IOException {
        List<String> argvList = new LinkedList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token curr = tokens.get(i);
            if (curr.type == TokenType.LITERAL) {
                argvList.add(curr.value);
            } else if (curr.type == TokenType.REDIRECT) {
                if (curr.value.startsWith("1") || curr.value.startsWith(">")) {//output redirect
                    if (i + 1 >= tokens.size()) throw new IllegalArgumentException("expected a file argument.");
                    Token next = tokens.get(i + 1);
                    if (next.type != TokenType.LITERAL)
                        throw new IllegalArgumentException("expected a file name after: %s".formatted(curr.value));

                    this.outputStreamWriter = BufferedWriterWrapper.fromFile(next.value); //throws IOException
                    this.redirectStandardOutput = true;
                }
                break;
            }
        }
        this.argv = argvList.toArray(new String[0]);
    }

    public int run() {
        if (argv.length == 0) return 0;
        try {
            Executable exec = BuiltIn.fromCommand(argv[0]);
            if (exec == null) {
                try {
                    exec = new ExternalProgram(argv[0], Navigation.getWorkingDir());
                } catch (FileNotFoundException e) {
                    errorStreamWriter.writeln("%s: %s".formatted(argv[0], Constants.COMMAND_NOT_FOUND)); // error caught my outside try-catch
                    return 1;
                }
            }
            InputOutputErrorStreams streams = new InputOutputErrorStreams(inputStreamReader, outputStreamWriter, errorStreamWriter);
            exec.execute(argv, streams);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            closeStreams();
        }
    }


//    private static String[] getArgv(String command) {
//        List<String> argv = new LinkedList<>();
//        //todo 'quote>' functionality
//        getArgv(command, 0, argv);
//        return argv.toArray(new String[0]);
//    }

    /**
     * @throws IllegalArgumentException if there is syntax error in provided command arguments
     */
    public static List<Token> tokenize(String command) throws IllegalArgumentException {
        List<Token> ls = new LinkedList<>();
        if (command.isEmpty()) return ls;
        boolean singleQuoteOpen = false;
        boolean doubleQuoteOpen = false;
        int i = 0;
        TokenType tokenType = TokenType.LITERAL;
        StringBuilder sb = new StringBuilder();
        while (i < command.length()) {
            char currChar = command.charAt(i++);
            if (currChar == '\'' && !doubleQuoteOpen) {
                singleQuoteOpen = !singleQuoteOpen;
            } else if (currChar == '\"' && !singleQuoteOpen) {
                doubleQuoteOpen = !doubleQuoteOpen;
            } else if (currChar == ' ' && !(singleQuoteOpen || doubleQuoteOpen)) {
                if (!sb.isEmpty()) {
                    ls.add(new Token(tokenType, sb.toString()));
                }
                sb = new StringBuilder();
                tokenType = TokenType.LITERAL;
            } else if (currChar == '>' && !(doubleQuoteOpen || singleQuoteOpen)) {
                tokenType = TokenType.REDIRECT;
                sb.append(currChar);
            } else if (currChar == '\\' && !singleQuoteOpen && !doubleQuoteOpen) { // outside of single and double quotes (escape)
                if (i >= command.length()) {
                    throw new IllegalArgumentException("Expected the escaped character at the end");
                }
                currChar = command.charAt(i++);
                sb.append(currChar);
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
        return ls;
    }


    private void closeStreams() {
        try {
            if (redirectStandardError) errorStreamWriter.close();
            if (redirectStandardOutput) outputStreamWriter.close();
            if (redirectStandardInput) inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
