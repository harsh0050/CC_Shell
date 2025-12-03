package shell;

import shell.io.InputOutputErrorStreams;
import shell.io.ShellOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Command {
    private String[] argv;
    private ShellOutputStream outputStream;
    private ShellOutputStream errorStream;
    private ShellOutputStream inputStream;

    private Command(List<Token> tokens) throws IllegalArgumentException, IOException {
        this.outputStream = new ShellOutputStream(true);
        this.errorStream = new ShellOutputStream(true);
        this.inputStream = null;
//        this.inputStreamReader = System.in;
        //HERE
        initFromTokens(tokens);
    }

    public int run() throws IOException {
        if (argv.length == 0) return 0;
        try {
            Executable exec = BuiltIn.fromCommand(argv[0]);
            if (exec == null) {
                exec = new ExternalProgram(argv[0], Navigation.getWorkingDir());
            }
            InputOutputErrorStreams streams = new InputOutputErrorStreams(inputStream, outputStream, errorStream);
            exec.execute(argv, streams);
            flushStreams();
            return 0;
        } catch (FileNotFoundException e) {
            errorStream.println("%s: %s".formatted(argv[0], Constants.COMMAND_NOT_FOUND)); // error caught by outside try-catch
            //may throw ioexception (internal error 500)
            return 1;
        } catch (Exception e) {
            e.printStackTrace(System.err);
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
                        this.outputStream = new ShellOutputStream(next.value, append); //throws FileNotFoundException
                    }
                    case "2>", "2>>" -> {
                        this.errorStream = new ShellOutputStream(next.value, append); //throws FileNotFoundException
                    }
                }
                break;
            }
        }
        this.argv = argvList.toArray(new String[0]);
    }


    //todo 'quote>' functionality

    private void closeStreams() throws IOException {
        errorStream.close();
        outputStream.close();
    }

    private void flushStreams() throws IOException {
        errorStream.flush();
        outputStream.flush();
    }

    public static List<Command> getCommands(String commandString) throws IOException {
        List<Token> tokens = InputTokenizer.tokenize(commandString);
        List<Command> commands = new ArrayList<>();
        List<Token> subCommand = new ArrayList<>();

        for(Token token: tokens){
            if(token.type != TokenType.PIPE){
                subCommand.add(token);
                continue;
            }
            commands.add(new Command(subCommand));
            subCommand.clear();
        }
        return commands;
    }

}
