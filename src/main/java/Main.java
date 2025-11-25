import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String commandString = s.nextLine();
            Command command = new Command(commandString);
            command.run();
        }
    }

}
