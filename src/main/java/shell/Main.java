package shell;

import shell.io.ShellLineReader;

import java.util.List;
import java.util.Scanner;

public class Main {

    static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        try {
            ShellLineReader lineReader = new ShellLineReader();
            while (true) {
                String commandString = lineReader.readLine("$ ");
                List<Command> commands = Command.getCommands(commandString);
                if(commands.size() == 1){
                    commands.getFirst().run();
                    continue;
                }
                if(commands.size() == 2){
//                    commands.getFirst().
                }
//                shell.Command command = new shell.Command(commandString);
//                command.run();
            }
        } catch (Exception e) {
            if (!e.getLocalizedMessage().isBlank()) {
                System.err.println(e.getLocalizedMessage());
            } else {
                e.printStackTrace(System.err);
            }
        }
    }


}


