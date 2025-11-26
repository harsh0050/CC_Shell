import java.io.BufferedReader;

public class InputOutputErrorStreams {
    public BufferedReader in;
    public PrintStreamWrapper out;
    public PrintStreamWrapper err;

    public InputOutputErrorStreams(BufferedReader in, PrintStreamWrapper out, PrintStreamWrapper err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

}

