import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String command = s.nextLine();
            try{
                evaluate(command);
            }catch (Exception e){
                System.out.println(command + ": " + e.getMessage());
            }
        }

    }

    public static void evaluate(String command) throws Exception {
        String[] argv = getArgv(command);

        if (argv[0].equals("exit")) {
            evaluateExit(argv);
            return;
        } else if (argv[0].equals("echo")) {
            evaluateEcho(argv);
            return;
        }
        throw new Exception("command not found");
    }

    public static void evaluateExit(String[] argv) throws Exception {
        if (argv.length < 2) {
            throw new Exception("too few arguments");
        }
        if (argv.length > 2) {
            throw new Exception("too many arguments");
        }
        if (argv[1].equals("0") || argv[1].equals("1")) {
            int exitCode = Integer.parseInt(argv[1]);
            System.exit(exitCode);
        } else {
            throw new Exception("invalid exit code");
        }
    }

    public static void evaluateEcho(String[] argv) throws Exception {
        if(argv.length < 2) throw new Exception("too few arguments");
        for(int i = 1; i<argv.length - 1; i++) System.out.print(argv[i] + " ");
        System.out.println(argv[argv.length - 1]);
    }

    public static String[] getArgv(String command){
        return command.split(" +");
    }
}
