import org.jline.reader.LineReader;
import org.jline.reader.impl.history.DefaultHistory;

import java.io.*;
import java.nio.file.Path;
import java.time.Instant;

public class ShellHistory extends DefaultHistory {
    private PrintStream historyPrintStream;
    private static ShellHistory INSTANCE = null;

    private ShellHistory() {
    }

    private ShellHistory(PrintStream historyPrintStream) {
        this.historyPrintStream = historyPrintStream;
    }

    public static ShellHistory getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new ShellHistory(new PrintStream(new FileOutputStream(Constants.HISTORY_FILE_PATH, true)));
            INSTANCE.loadHistory();
        }
        return INSTANCE;
    }

    private void loadHistory() throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(Constants.HISTORY_FILE_PATH))){
            String line;
            while((line = reader.readLine()) != null){
                super.add(Instant.MIN, line);
            }
        }
    }
}
