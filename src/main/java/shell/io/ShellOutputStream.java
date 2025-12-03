package shell.io;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ShellOutputStream {
    private final OutputStream out;
    private final boolean shouldClose;
    private final boolean isStandardOut;

    public ShellOutputStream(boolean isStandardOut) {
        this(null, false, true);
    }

    public ShellOutputStream(OutputStream out) {
        this(out, true, false);
    }

    public ShellOutputStream(String filePath, boolean append) throws FileNotFoundException {
        OutputStream out = new FileOutputStream(filePath, append);
        this(out, true, false);
    }

    private ShellOutputStream(OutputStream out, boolean shouldClose, boolean isStandardOut) {
        this.out = out;
        this.shouldClose = shouldClose;
        this.isStandardOut = isStandardOut;
    }

    public void println(String str) throws IOException {
        this.print(str + "\n");
    }

    public void print(String str) throws IOException {
        this.write(str.getBytes(Charset.defaultCharset()));
    }

    public void write(byte[] b) throws IOException {
        for (byte bt : b) {
            this.write(bt);
        }
    }

    public void write(int b) throws IOException {
        if (isStandardOut) {
            System.out.write(b);
            return;
        }
        this.out.write(b);
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void close() throws IOException {
        if (shouldClose)
            out.close();
    }

}
