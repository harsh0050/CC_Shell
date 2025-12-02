import java.util.Scanner;

public class Main {

    static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        try {
            ShellLineReader lineReader = new ShellLineReader();
            while (true) {
                String commandString = lineReader.readLine("$ ");
                Command command = new Command(commandString);
                command.run();
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


