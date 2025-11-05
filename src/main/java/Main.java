import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        System.out.print("$ ");
        String command = s.nextLine();
        System.out.println(command + ": command not found");
    }
}
