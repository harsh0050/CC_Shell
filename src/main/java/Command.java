import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Command {
    private String[] argv;
    private BufferedWriterWrapper outputStreamWriter;
    private BufferedWriterWrapper errorStreamWriter;
    private BufferedReader inputStreamReader;

    public Command(String command) throws IllegalArgumentException, IOException {
        this.outputStreamWriter = BufferedWriterWrapper.fromPrintStream(System.out, true, false);
        this.errorStreamWriter = BufferedWriterWrapper.fromPrintStream(System.err, true, false);
        this.inputStreamReader = new BufferedReader(new InputStreamReader(System.in));

        List<Token> tokens = InputTokenizer.tokenize(command);
        initFromTokens(tokens);
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
            outputStreamWriter.flush();
            errorStreamWriter.flush();
            return 0;
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
                if (curr.value.startsWith("1") || curr.value.startsWith(">")) {//output redirect
                    Token next = tokens.get(i + 1);
                    this.outputStreamWriter = BufferedWriterWrapper.fromFile(next.value, false, true); //throws IOException
                } else if (curr.value.startsWith("2")) {
                    Token next = tokens.get(i + 1);
                    this.errorStreamWriter = BufferedWriterWrapper.fromFile(next.value, false, true); //throws IOException
                }
                break;
            }
        }
        this.argv = argvList.toArray(new String[0]);
    }


    //todo 'quote>' functionality

    private void closeStreams() {
        try {
            errorStreamWriter.close();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
