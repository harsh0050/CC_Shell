import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Command {
    private String[] argv;
    private PrintStreamWrapper outputStream;
    private PrintStreamWrapper errorStream;
    private BufferedReader inputStreamReader;

    public Command(String command) throws IllegalArgumentException, IOException {
        this.outputStream = new PrintStreamWrapper(System.out, false);
        this.errorStream = new PrintStreamWrapper(System.err, false);
        this.inputStreamReader = new BufferedReader(new InputStreamReader(System.in));

        List<Token> tokens = InputTokenizer.tokenize(command);
        initFromTokens(tokens);
    }

    public int run() {
        if (argv.length == 0) return 0;
        try {
            Executable exec = BuiltIn.fromCommand(argv[0]);
            if (exec == null) {
                exec = new ExternalProgram(argv[0], Navigation.getWorkingDir());
            }
            InputOutputErrorStreams streams = new InputOutputErrorStreams(inputStreamReader, outputStream, errorStream);
            exec.execute(argv, streams);
            flushStreams();
            return 0;
        } catch (FileNotFoundException e) {
            errorStream.println("%s: %s".formatted(argv[0], Constants.COMMAND_NOT_FOUND)); // error caught my outside try-catch
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            closeStreams();
        }
    }

    private void initFromTokens(List<Token> tokens) throws IllegalArgumentException, IOException {
        List<String> argvList = new LinkedList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token curr = tokens.get(i);
            if (curr.type == TokenType.LITERAL) {
                argvList.add(curr.value);
            } else if (curr.type == TokenType.REDIRECT) {
                Token next = tokens.get(i + 1);
                boolean append = curr.value.contains(">>");
                switch (curr.value) {
                    case "1>", ">", "1>>", ">>" -> {
                        this.outputStream = new PrintStreamWrapper(next.value, true, append); //throws IOException
                    }
                    case "2>", "2>>" -> {
                        this.errorStream = new PrintStreamWrapper(next.value, true, append); //throws IOException
                    }
                }
                break;
            }
        }
        this.argv = argvList.toArray(new String[0]);
    }


    //todo 'quote>' functionality

    private void closeStreams() {
        errorStream.close();
        outputStream.close();
    }

    private void flushStreams() {
        errorStream.flush();
        outputStream.flush();
    }

}
