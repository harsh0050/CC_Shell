import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ExternalProgram implements Executable {
    private String workingDir;

    /**
     * @throws FileNotFoundException If a file with the name {@code command} is not found in directories mentioned in {@code PATH}
     */
    public ExternalProgram(String command, String workingDir) throws FileNotFoundException {
        this.workingDir = workingDir;
        if (getFilePath(command) == null) {
            throw new FileNotFoundException();

        }
    }

    public int execute(String... argv){
        String execPath = getFilePath(argv[0]);
        if (execPath == null) {
            System.out.println("%s: %s".formatted(argv[0], Constants.COMMAND_NOT_FOUND));
            return 1;
        }
        File dir = new File(workingDir);
        ProcessBuilder pb = new ProcessBuilder(argv).directory(dir).inheritIO();
        try{
            return pb.start().waitFor();
        }catch (Exception e){
            System.out.println(Arrays.toString(argv));
            e.printStackTrace();
            return 1;
        }
    }

    public static String getFilePath(String command) {
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
