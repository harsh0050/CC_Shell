import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class InputOutputErrorStreams {
    public BufferedReader in;
    public BufferedWriterWrapper out;
    public BufferedWriterWrapper err;

    public InputOutputErrorStreams(BufferedReader in, BufferedWriterWrapper out, BufferedWriterWrapper err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

}

