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
    public static void setWorkingDir(String path) throws NoSuchFileException {
        try{
            if (path.startsWith(File.separator)) {
                setAbsolutePath(path);
            }else{
                setRelativePath(path);
            }
        }catch (NoSuchFileException e){
            throw new NoSuchFileException(path +": " + e.getMessage());
        }
    }

    private static void setRelativePath(String path) throws NoSuchFileException{
        String newPath = workingDir.toString() + File.separator + path;
        setAbsolutePath(newPath);
    }

    private static void setAbsolutePath(String path) throws NoSuchFileException {
        if (!Files.isDirectory(Path.of(path))) {
            throw new NoSuchFileException("No such file or directory");
        }

        workingDir = Path.of(path).normalize();
    }

    public static String getWorkingDir() {
        return workingDir.toString();
//        return null;
    }
}
