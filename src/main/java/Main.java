import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    static Set<String> builtinCommandsSet;
    static {
        builtinCommandsSet = new HashSet<>(Arrays.asList("echo", "exit", "type"));
    }
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String command = s.nextLine();
            String[] argv = getArgv(command);
            try{
                evaluate(argv);
            }catch (Exception e){
                System.out.println(argv[0] + ": " + e.getMessage());
            }
        }


    }

    public static void evaluate(String[] argv) throws Exception {
        if (argv[0].equals("exit")) {
            evaluateExit(argv);
            return;
        } else if (argv[0].equals("echo")) {
            evaluateEcho(argv);
            return;
        } else if(argv[0].equals("type")){
            evaluateType(argv);
            return;
        }
        throw new Exception("command not found");
    }

    public static void evaluateExit(String[] argv) throws Exception {
        Util.validateArgumentCount(2, argv.length);
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

    public static void evaluateType(String[] argv) throws Exception {
        Util.validateArgumentCount(2, argv.length);
        if(builtinCommandsSet.contains(argv[1])){
            System.out.println(argv[1] + " is a shell builtin");
            return;
        } else {
            String path = getExecutablePath(argv[1]);
            if(path != null){
                System.out.println(argv[1] + " is " + path);
                return;
            }
        }
        System.out.println(argv[1] + ": not found");
    }

    public static String[] getArgv(String command){
        return command.split(" +");
    }

    public static String getExecutablePath(String command){
        String[] paths = System.getenv("PATH").split(File.pathSeparator);
        for(String path: paths){
            if (Files.isDirectory(Path.of(path))) {
                File f = new File(path + File.separator + command);
                if(f.exists() && f.canExecute()) return f.getAbsolutePath();
            }
        }
        return null;
    }
}
