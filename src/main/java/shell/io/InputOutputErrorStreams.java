package shell.io;

public class InputOutputErrorStreams {
    public ShellOutputStream in;
    public ShellOutputStream out;
    public ShellOutputStream err;

    public InputOutputErrorStreams(ShellOutputStream in, ShellOutputStream out, ShellOutputStream err) {
        this.in = in;
        this.out = out;
        this.err = err;

    }

}

