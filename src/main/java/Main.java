import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        while (true){
            System.out.print("$ ");
            String command = s.nextLine();
            evaluate(command);
        }

    }

    public static void evaluate(String command){
        if(command.startsWith("exit")){
            //TODO validate for correctness
            System.exit(command.charAt(5) - '0');
            return;
        }
        System.out.println(command + ": command not found");
    }
}
