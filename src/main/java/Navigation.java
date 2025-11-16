import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Navigation {
    private static Path workingDir = Path.of(System.getProperty("user.dir"));
    private static final String userHomeDir = System.getProperty("user.home");

    /**
     * @throws NoSuchFileException if the path is not valid.
     */
    public static void setWorkingDir(String pathString) throws NoSuchFileException {
        try{
            Path path = Path.of(pathString);
            if (path.startsWith(File.separator)) {
                setAbsolutePath(path);
            }else{
                setRelativePath(path);
            }
        }catch (NoSuchFileException e){
            throw new NoSuchFileException(null);
        }
    }

    private static void setRelativePath(Path path) throws NoSuchFileException{
        setAbsolutePath(workingDir.resolve(path));
    }

    private static void setAbsolutePath(Path path) throws NoSuchFileException {
        if (!Files.isDirectory(path)) {
            throw new NoSuchFileException(null);
        }

        workingDir = path.normalize();
    }

    public static String getWorkingDir() {
        return workingDir.toString();
//        return null;
    }
}
