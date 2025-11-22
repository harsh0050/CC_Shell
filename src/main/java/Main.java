import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

//    static Set<String> builtinCommandsSet = new HashSet<>(Arrays.asList("echo", "exit", "type", "pwd", "cd"));

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
                e.printStackTrace();
            }
        }
    }

    public static void evaluate(String[] argv) throws InterruptedException, IOException {
        BuiltIn builtIn = BuiltIn.fromCommand(argv[0]);

        Result res = switch (builtIn) {
            case EXIT -> evaluateExit(argv);
            case ECHO -> evaluateEcho(argv);
            case TYPE -> evaluateType(argv);
            case PWD -> evaluatePwd(argv);
            case CD -> evaluateCd(argv);
            case null -> runExecutable(argv, Navigation.getWorkingDir());
        };

        switch (res) {
            case Success _ -> {
            }
            case Failure f -> {
                System.out.println(f.message);
            }
            case Pending p -> {
                p.resolve();
            }
        }
    }

    public static Result evaluateExit(String... argv) {
        if(argv.length > 2) {
            return new Failure("%s: %s".formatted(argv[0], Constants.TOO_MANY_ARGUMENTS));
        }
        if(argv.length == 1) {
            System.exit(0);
        }
        if (argv[1].equals("0") || argv[1].equals("1")) {
            int exitCode = Integer.parseInt(argv[1]);
            System.exit(exitCode);
        }
        return new Failure(argv[0] + ": invalid exit code");
    }

    public static Result evaluateEcho(String... argv) {
        if (argv.length < 2) return new Failure("%s: %s".formatted(argv[0], Constants.TOO_FEW_ARGUMENTS));
        for (int i = 1; i < argv.length - 1; i++) System.out.print(argv[i] + " ");
        System.out.println(argv[argv.length - 1]);
        return new Success();
    }

    public static Result evaluateType(String... argv) {
        if (Util.validateArgumentCount(2, argv.length) instanceof Failure f) {
            return new Failure(argv[0] + ": " + f.message);
        }
        if (BuiltIn.fromCommand(argv[1]) != null) {
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
        if(Util.validateArgumentCount(1, argv.length) instanceof Failure f){
            return new Failure(argv[0] + ": "+ f.message);
        }
        System.out.println(Navigation.getWorkingDir());
        return new Success();
    }

    public static Result evaluateCd(String... argv) {
        if(Util.validateArgumentCount(2, argv.length) instanceof Failure f){
            return new Failure(argv[0] + ": "+ f.message);
        }
        String path = argv[1];
        try {
            Navigation.setWorkingDir(path);
            return new Success();
        } catch (NoSuchFileException e) {
            return new Failure("%s: %s: %s".formatted(argv[0], argv[1], Constants.NO_SUCH_FILE_OR_DIRECTORY));
        }
    }

    public static String[] getArgv(String command) {
        return command.split(" +");
    }

    public static Result runExecutable(String[] argv, String workingDir) throws IOException {
        String execPath = getExecutablePath(argv[0]);
        if (execPath == null) {
            return new Failure("%s: %s".formatted(argv[0], Constants.COMMAND_NOT_FOUND));
        }
        File dir = new File(workingDir);
        ProcessBuilder pb = new ProcessBuilder(argv).inheritIO().directory(dir);
        return new Pending(pb.start());
    }

    public static String getExecutablePath(String command) {
        String pathsString = System.getenv(Constants.PATH_ENV_IDENTIFIER);
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
