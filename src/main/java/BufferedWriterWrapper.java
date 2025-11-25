import java.io.*;

public class BufferedWriterWrapper{
    public final BufferedWriter writer;

    public BufferedWriterWrapper(BufferedWriter writer) {
        this.writer = writer;
    }

    public static BufferedWriterWrapper fromPrintStream(PrintStream printStream){
        return new BufferedWriterWrapper(new BufferedWriter(new OutputStreamWriter(printStream)));
    }
    public static BufferedWriterWrapper fromFile(String filePath) throws IOException{
        return new BufferedWriterWrapper(new BufferedWriter(new FileWriter(filePath)));
    }


    //todo memory stuff, buffers
    public void writeln(String str) throws IOException {
        writer.write(str);
        writer.newLine();
        writer.flush();
    }

    public void writeln() throws IOException {
        writer.newLine();
        writer.flush();
    }

    /**Writes to the stream without flushing. to flush, call {@code flush}*/
    public void write(String str) throws IOException {
        writer.write(str);
    }

    /**Writes to the stream without flushing. to flush, call {@code writeln}*/
    public void write(int b) throws IOException {
        writer.write(b);
    }

    public void flush() throws IOException{
        writer.flush();
    }


    public void close() throws IOException {
        writer.close();
    }

}