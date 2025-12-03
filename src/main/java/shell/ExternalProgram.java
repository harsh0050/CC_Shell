package shell;

import shell.io.InputOutputErrorStreams;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ExternalProgram implements Executable {
    private final String workingDir;

    /**
     * @throws FileNotFoundException If a file with the name {@code command} is not found in directories mentioned in {@code PATH}
     */
    public ExternalProgram(String command, String workingDir) throws FileNotFoundException {
        this.workingDir = workingDir;
        if (getFilePath(command) == null) {
            throw new FileNotFoundException();
        }
    }

    public int execute(String[] argv, InputOutputErrorStreams streams) throws IOException {
        String execPath = getFilePath(argv[0]);
        if (execPath == null) {
            streams.out.println("%s: %s".formatted(argv[0], Constants.COMMAND_NOT_FOUND));
            return 1;
        }
        File dir = new File(workingDir);
        //todo add support for input redirection (from something to this command)
        ProcessBuilder pb = new ProcessBuilder(argv).directory(dir).redirectInput(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p = pb.start();
            Thread t1 = new Thread(() -> {
                try (InputStream errorStream = p.getErrorStream()) {
                    int data;
                    while ((data = errorStream.read()) != -1) {
                        streams.err.write(data);
                    }
                    streams.err.flush();
                } catch (IOException e) { //todo handle every caught exception (in future)
                    e.printStackTrace();
                }
            });

            Thread t2 = new Thread(() -> {
                try (InputStream inputStream = p.getInputStream()) {
                    int data;
                    while ((data = inputStream.read()) != -1) {
                        streams.out.write(data);
                    }
                    streams.out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
//            OutputStream op = OutputStream.nullOutputStream();
//            op.wri
//            new PipedInputStream()
            return p.waitFor();
        } catch (Exception e) {
            streams.err.println(Arrays.toString(argv));
            for(StackTraceElement stackTraceElement : e.getStackTrace()){
                streams.err.println(stackTraceElement.toString());
            }
            return 1;
        }
    }

    public static String getFilePath(String command) {
        String pathsString = System.getenv(Constants.PATH_ENV_IDENTIFIER);
        if (pathsString == null) return null;
        List<String> validDirs = getValidDirPaths(pathsString);
        for(String path: validDirs){
            File f = new File(path + File.separator + command);
            if (f.exists() && f.canExecute()) return f.getAbsolutePath();
        }
        return null;
    }

    public static List<String> getValidDirPaths(String allPaths) {
        List<String> ls = new LinkedList<>();
        String[] paths = allPaths.split(File.pathSeparator);
        for (String path : paths) {
            if (path.trim().isEmpty()) continue;
            if (Files.isDirectory(Path.of(path))) {
                ls.add(path);
            }
        }
        return ls;
    }
}
