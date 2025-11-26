import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String commandString = s.nextLine();

            try {
                Command command = new Command(commandString);
                command.run();
            } catch (Exception e){
                if(!e.getLocalizedMessage().isBlank()){
                    System.err.println(e.getLocalizedMessage());
                }else {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

}
