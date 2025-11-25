import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
//
        while (true) {
            System.out.print("$ ");
            String commandString = s.nextLine();
            Command command = new Command(commandString);
            command.run();
        }
    }

}
