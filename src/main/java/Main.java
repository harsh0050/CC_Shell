import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    static Set<String> builtinCommandsSet = new HashSet<>(Arrays.asList("echo", "exit", "type", "pwd"));
    static String workingDir = System.getProperty("user.dir");
    static String userHomeDir = System.getProperty("user.home");
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
//        System.out.writeBytes(R.readAllBytes());
//        runExecutable("ls", "hihihi");
//        System.out.println(System.getProperty("user.dir"));

        while (true) {
            System.out.print("$ ");
            String command = s.nextLine();
            if(command.isBlank()) continue;
            String[] argv = getArgv(command);
            try{
                evaluate(argv);
            }catch (Exception e){
                System.out.println(argv[0] + ": " + e.getMessage());
            }
        }
    }

    public static void evaluate(String[] argv) throws Exception {
        switch (argv[0]) {
            case "exit" -> evaluateExit(argv);
            case "echo" -> evaluateEcho(argv);
            case "type" -> evaluateType(argv);
            case "pwd" -> evaluatePwd(argv);
            case "cd" -> evaluateCd(argv);
            default -> {
                String execPath = getExecutablePath(argv[0]);
                if (execPath == null) {
                    throw new Exception("command not found");
                }
//                String[] copyOfArgv = Arrays.copyOf(argv, argv.length);
//                copyOfArgv[0] = execPath;
                runExecutable(argv).waitFor();
            }
        }
    }

    public static void evaluateExit(String... argv) throws Exception {
        Util.validateArgumentCount(2, argv.length);
        if (argv[1].equals("0") || argv[1].equals("1")) {
            int exitCode = Integer.parseInt(argv[1]);
            System.exit(exitCode);
        } else {
            throw new Exception("invalid exit code");
        }
    }

    public static void evaluateEcho(String... argv) throws Exception {
        if(argv.length < 2) throw new Exception("too few arguments");
        for(int i = 1; i<argv.length - 1; i++) System.out.print(argv[i] + " ");
        System.out.println(argv[argv.length - 1]);
    }

    public static void evaluateType(String... argv) throws Exception {
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

    public static void evaluatePwd(String... argv) throws Exception{
        Util.validateArgumentCount(1, argv.length);
        System.out.println(workingDir);
    }

    public static void evaluateCd(String... argv) throws Exception {
        Util.validateArgumentCount(2, argv.length);
        String path = argv[1];
        if(path.startsWith(File.separator)){
            if(!Files.isDirectory(Path.of(path))){
                throw new Exception("%s: No such file or directory".formatted(path));
            }
            workingDir = path;
        }
    }
    public static String[] getArgv(String command){
        return command.split(" +");
    }

    public static Process runExecutable(String[] argv) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(argv).inheritIO();
        return pb.start();
    }
    public static String getExecutablePath(String command){
        String pathsString = System.getenv("PATH");
        if(pathsString == null) return null;
        String[] paths = pathsString.split(File.pathSeparator);
        for(String path: paths){
            if(path.trim().isEmpty()) continue;
            if (Files.isDirectory(Path.of(path))) {
                File f = new File(path + File.separator + command);
                if(f.exists() && f.canExecute()) return f.getAbsolutePath();
            }
        }
        return null;
    }
}
