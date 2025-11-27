import org.jline.builtins.Completers;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.EnumCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.sound.sampled.Line;
import java.io.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        try {
            LineReader reader = buildLineReader();
//            System.out.println(reader.readLine("$ "));
            while (true) {
                String commandString = reader.readLine("$ ");
                Command command = new Command(commandString);
                command.run();
            }
        } catch (Exception e) {
            if (!e.getLocalizedMessage().isBlank()) {
                System.err.println(e.getLocalizedMessage());
            } else {
                e.printStackTrace(System.err);
            }
        }
    }

    private static LineReader buildLineReader() throws IOException {
        LinkedList<Completer> completers = new LinkedList<>();
        completers.add(new EnumCompleter(BuiltIn.class));

        String allPaths = System.getenv(Constants.PATH_ENV_IDENTIFIER);
        if (allPaths == null) return null;
        for (String path : ExternalProgram.getValidDirPaths(allPaths)) {
            completers.add(new Completers.FilesCompleter(Path.of(path)));
        }
        Completer completer = new AggregateCompleter(completers);

        return LineReaderBuilder.builder()
                .completer(completer)
                .parser(new DefaultParser().escapeChars(null))
                .terminal(TerminalBuilder.builder().build())
                .history(ShellHistory.getInstance())
                .option(LineReader.Option.HISTORY_IGNORE_DUPS, false)
                .option(LineReader.Option.HISTORY_TIMESTAMPED, false)
                .option(LineReader.Option.COMPLETE_MATCHER_TYPO, false)
                .variable(LineReader.HISTORY_FILE, Path.of(Constants.HISTORY_FILE_PATH))
                .build();
    }

}


