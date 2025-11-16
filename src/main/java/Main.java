import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    static Set<String> builtinCommandsSet = new HashSet<>(Arrays.asList("echo", "exit", "type", "pwd", "cd"));

    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String command = s.nextLine();
            if (command.isBlank()) continue;
            String[] argv = getArgv(command);
            try {
                evaluate(argv);
            } catch (Exception e) {
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

    public static Result evaluateExit(String... argv) {
        if (Util.validateArgumentCount(2, argv.length) instanceof Failure f) {
            return new Failure(argv[0] + ": " + f.message);
        }
        if (argv[1].equals("0") || argv[1].equals("1")) {
            int exitCode = Integer.parseInt(argv[1]);
            System.exit(exitCode);
        }
        return new Failure(argv[0] + ": invalid exit code");
    }

    public static Result evaluateEcho(String... argv) {
        if (argv.length < 2) return new Failure(argv[0] + ": too few arguments");
        for (int i = 1; i < argv.length - 1; i++) System.out.print(argv[i] + " ");
        System.out.println(argv[argv.length - 1]);
        return new Success();
    }

    public static Result evaluateType(String... argv) {
        if (Util.validateArgumentCount(2, argv.length) instanceof Failure f) {
            return new Failure(argv[0] + ": " + f.message);
        }
        if (builtinCommandsSet.contains(argv[1])) {
            System.out.println(argv[1] + " is a shell builtin");
            return new Success();
        } else {
            String path = getExecutablePath(argv[1]);
            if (path != null) {
                System.out.println(argv[1] + " is " + path);
                return new Success();
            }
        }
        return new Failure(argv[1] + ": not found");
    }

    public static Result evaluatePwd(String... argv) {
        Util.validateArgumentCount(1, argv.length);
        System.out.println(Navigation.getWorkingDir());
        return new Success();
    }

    public static Result evaluateCd(String... argv) {
        Util.validateArgumentCount(2, argv.length);
        String path = argv[1];
        try {
            Navigation.setWorkingDir(path);
            return new Success();
        }catch (NoSuchFileException e){
            return new Failure("%s: %s: No such file or directory".formatted(argv[0], argv[1]));
        }
    }

    public static String[] getArgv(String command) {
        return command.split(" +");
    }

    public static Process runExecutable(String[] argv) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(argv).inheritIO();
        return pb.start();
    }

    public static String getExecutablePath(String command) {
        String pathsString = System.getenv("PATH");
        if (pathsString == null) return null;
        String[] paths = pathsString.split(File.pathSeparator);
        for (String path : paths) {
            if (path.trim().isEmpty()) continue;
            if (Files.isDirectory(Path.of(path))) {
                File f = new File(path + File.separator + command);
                if (f.exists() && f.canExecute()) return f.getAbsolutePath();
            }
        }
        return null;
    }
}
