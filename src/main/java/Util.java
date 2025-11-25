import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Util {
    public static void printStackTrace(Exception e, BufferedWriterWrapper writer) {
        try {
            for(StackTraceElement element: e.getStackTrace()){
                writer.writeln(element.toString());
            }
        } catch (IOException ex) {
            System.out.println("Util:printStackTrace");
            ex.printStackTrace();
        }
    }
}
