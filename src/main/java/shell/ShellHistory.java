package shell;

import org.jline.reader.impl.history.DefaultHistory;

import java.io.*;
import java.time.Instant;

public class ShellHistory extends DefaultHistory {
    private PrintStream historyPrintStream;
    private static ShellHistory INSTANCE = null;

    private ShellHistory() {
    }

    private ShellHistory(PrintStream historyPrintStream) {
        this.historyPrintStream = historyPrintStream;
    }

    public static ShellHistory getInstance() throws RuntimeException {
        return Holder.INSTANCE;
    }

    private void loadHistory() throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(Constants.HISTORY_FILE_PATH))){
            String line;
            while((line = reader.readLine()) != null){
                super.add(Instant.MIN, line);
            }
        }
    }

    private static class Holder{
        static final ShellHistory INSTANCE;

        static {
            try {
                INSTANCE = new ShellHistory(new PrintStream(new FileOutputStream(Constants.HISTORY_FILE_PATH, true)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
