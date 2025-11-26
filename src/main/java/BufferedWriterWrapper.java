import java.io.*;

public class BufferedWriterWrapper{
    public final BufferedWriter writer;
    private final boolean autoFlush;
    private final boolean shouldClose;

    /**@param shouldClose if true, a call to {@code close} function will close the underlying stream.*/
    public BufferedWriterWrapper(BufferedWriter writer, boolean autoFlush, boolean shouldClose) {
        this.writer = writer;
        this.autoFlush = autoFlush;
        this.shouldClose = shouldClose;
    }

    public static BufferedWriterWrapper fromPrintStream(PrintStream printStream, boolean autoFlush, boolean shouldClose){
        return new BufferedWriterWrapper(new BufferedWriter(new OutputStreamWriter(printStream)), autoFlush, shouldClose);

    }
    public static BufferedWriterWrapper fromFile(String filePath, boolean autoFlush, boolean shouldClose) throws IOException{
        return new BufferedWriterWrapper(new BufferedWriter(new FileWriter(filePath)), autoFlush, shouldClose);
    }


    //todo memory stuff, buffers
    /**Writes text with a new line and flushes immediately if {@code autoFlush} is enabled.*/
    public void writeln(String str) throws IOException {
        writer.write(str);
        writeln();
    }

    /**Writes a new line and flushes immediately if {@code autoFlush} is enabled.*/
    public void writeln() throws IOException {
        writer.newLine();
        if(autoFlush) writer.flush();
    }

    /**Writes to the stream without flushing. to flush, call {@code flush}*/
    public void write(String str) throws IOException {
        writer.write(str);
    }

    /**Writes to the stream without flushing. to flush, call {@code flush}*/
    public void write(int b) throws IOException {
        writer.write(b);
    }

    public void flush() throws IOException{
        writer.flush();
    }

    public void close() throws IOException {
        if(shouldClose)
            writer.close();
    }

}