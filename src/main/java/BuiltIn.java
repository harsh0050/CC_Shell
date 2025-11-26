import java.io.IOException;
import java.nio.file.NoSuchFileException;

public enum BuiltIn implements Executable{
    ECHO("echo", 0, Integer.MAX_VALUE) {
        @Override
        public int execute(String[] argv, InputOutputErrorStreams streams) throws IOException {
            int code = super.execute(argv, streams);
            if (code != 0) return code;
            for (int i = 1; i < argv.length - 1; i++) {
                streams.out.print(argv[i] + " ");
            }
            streams.out.println(argv[argv.length - 1]);
            return 0;
        }
    },
    EXIT("exit", 0, 1) {
        @Override
        public int execute(String[] argv, InputOutputErrorStreams streams) throws IOException {
            int code = super.execute(argv, streams);
            if (code != 0) return code;
            if (argv.length == 1) {
                System.exit(0);
            }
            if (argv[1].equals("0") || argv[1].equals("1")) {
                int exitCode = Integer.parseInt(argv[1]);
                System.exit(exitCode);
            }
            streams.out.println(this.command + "invalid exit code");
            return 1;
        }
    },
    TYPE("type", 1, 1) {
        @Override
        public int execute(String[] argv, InputOutputErrorStreams streams) throws IOException {
            int code = super.execute(argv, streams);
            if (code != 0) return code;
            for (BuiltIn b : values()) {
                if (b.command.equals(argv[1])) {
                    streams.out.println(argv[1] + " is a shell builtin");
                    return 0;
                }
            }
            String path = ExternalProgram.getFilePath(argv[1]);
            if (path != null) {
                streams.out.println(argv[1] + " is " + path);
                return 0;
            }

            streams.err.println(argv[1] + ": not found");
            return 1;
        }
    },
    PWD("pwd", 0, 0) {
        @Override
        public int execute(String[] argv, InputOutputErrorStreams streams) throws IOException {
            int code = super.execute(argv, streams);
            if (code != 0) return code;

            streams.out.println(Navigation.getWorkingDir());
            return 0;
        }
    },
    CD("cd", 1, 1) {
        @Override
        public int execute(String[] argv, InputOutputErrorStreams streams) throws IOException {
            int code = super.execute(argv, streams);
            if (code != 0) return code;

            String path = argv[1];
            try {
                Navigation.setWorkingDir(path);
                return 0;
            } catch (NoSuchFileException e) {
                streams.out.println("%s: %s: %s".formatted(argv[0], argv[1], Constants.NO_SUCH_FILE_OR_DIRECTORY));
                return 1;
            }

        }
    };

    public final String command;
    private final int minArguments, maxArguments;

    BuiltIn(String command, int minArguments, int maxArguments) {
        this.command = command;
        this.maxArguments = maxArguments;
        this.minArguments = minArguments;
    }

    @Override
    public int execute(String[] argv, InputOutputErrorStreams streams) throws IOException{
        if (argv.length - 1 < minArguments) {
            System.out.println(Constants.TOO_FEW_ARGUMENTS);
            return 1;
        }
        if (argv.length - 1 > maxArguments) {
            System.out.println(Constants.TOO_MANY_ARGUMENTS);
            return 1;
        }
        return 0;
    }

    /**
     * @return Enum of the given command if it exists, otherwise {@code null}
     */
    public static BuiltIn fromCommand(String command) {
        for (BuiltIn b : values()) {
            if (b.command.equals(command)) {
                return b;
            }
        }
        return null;
    }
}

