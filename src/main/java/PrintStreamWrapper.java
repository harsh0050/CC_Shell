import java.io.*;
import java.nio.charset.StandardCharsets;

public class PrintStreamWrapper {
    private final PrintStream stream;
    private final boolean shouldClose;

    /**
     * @param shouldClose if true, a call to {@code close} function will close the underlying stream.
     */
    public PrintStreamWrapper(PrintStream stream, boolean shouldClose) {
        this.stream = stream;
        this.shouldClose = shouldClose;
    }

    public PrintStreamWrapper(String filePath, boolean shouldClose, boolean append) throws IOException {
        this(new PrintStream(new FileOutputStream(filePath, append), true), shouldClose);

    }
    //todo memory stuff, buffers

    public void println(String str) {
        stream.println(str);
    }

    public void print(String str) {
        stream.print(str);
    }

    public void write(int b) {
        stream.write(b);
    }

    public void flush() {
        stream.flush();
    }

    public void close() {
        if (shouldClose)
            stream.close();
    }

    public PrintStream getStream() {
        return stream;
    }
}