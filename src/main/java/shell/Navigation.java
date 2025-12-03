package shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Navigation {
    private static Path workingDir = Path.of(System.getProperty(Constants.CURR_DIR_SYS_PROPERTY_IDENTIFIER));
    private static final Path userHomeDir = Path.of(System.getenv(Constants.USER_HOME_ENV_IDENTIFIER));

    /**
     * @throws NoSuchFileException if the path is not valid.
     */
    public static void setWorkingDir(String pathString) throws NoSuchFileException {
        Path path = Path.of(pathString);
        if (path.startsWith(File.separator)) {
            setAbsolutePath(path);
        } else if (path.startsWith("~")) {
            setRelativePath(userHomeDir, Path.of(pathString.substring(1)));
        } else {
            setRelativePath(workingDir, path);
        }
    }

    private static void setRelativePath(Path from, Path path) throws NoSuchFileException {
        setAbsolutePath(from.resolve(path));
    }

    private static void setAbsolutePath(Path path) throws NoSuchFileException {
        if (!Files.isDirectory(path)) {
            throw new NoSuchFileException(Constants.NO_SUCH_FILE_OR_DIRECTORY);
        }

        workingDir = path.normalize();
    }

    public static String getWorkingDir() {
        return workingDir.toString();
    }
}
