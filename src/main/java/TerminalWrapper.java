import org.jline.builtins.Completers;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.EnumCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.impl.CursorSupport;
import org.jline.utils.InfoCmp;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class TerminalWrapper {
    private final Terminal terminal;
    private final LineReader lineReader;
    private final Completer completer;

    private TerminalWrapper() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();

        LinkedList<Completer> completers = new LinkedList<>();
        completers.add(new EnumCompleter(BuiltIn.class));

        String allPaths = System.getenv(Constants.PATH_ENV_IDENTIFIER);
        if (allPaths != null) {
            for (String path : ExternalProgram.getValidDirPaths(allPaths)) {
                completers.add(new Completers.FilesCompleter(Path.of(path)));
            }
        }
        completer = new AggregateCompleter(completers);

        lineReader = LineReaderBuilder.builder()
                .completer(completer)
                .parser(new DefaultParser().escapeChars(null))
                .terminal(terminal)
                .history(ShellHistory.getInstance())
                .option(LineReader.Option.HISTORY_IGNORE_DUPS, false)
                .option(LineReader.Option.HISTORY_TIMESTAMPED, false)
                .option(LineReader.Option.COMPLETE_MATCHER_TYPO, false)
                .option(LineReader.Option.MENU_COMPLETE, false)
                .variable(LineReader.HISTORY_FILE, Path.of(Constants.HISTORY_FILE_PATH))
                .build();

        lineReader.getKeyMaps().get(LineReader.MAIN).bind(getWidget(), "\t");

    }

    private Widget getWidget() {
        return new Widget() {
            int tabCount = 0;

            @Override
            public boolean apply() {
                tabCount++;
                ParsedLine line = lineReader.getParser().parse(lineReader.getBuffer().toString(), lineReader.getBuffer().length());
                List<Candidate> candidates = new LinkedList<>();
                completer.complete(lineReader, line, candidates);
                filterList(candidates, line.word());
//                System.out.println(candidates);
                terminal.puts(InfoCmp.Capability.save_cursor);
                terminal.writer().println();
                terminal.writer().print("\u001B[2K");

                terminal.writer().print(candidates);
                terminal.puts(InfoCmp.Capability.restore_cursor);
                terminal.flush();
                return false;
            }
        };
    }

    private void filterList(List<Candidate> candidates, String filter){
        List<Candidate> remove = new LinkedList<>();
        for(Candidate candidate: candidates){
            if(!candidate.value().startsWith(filter)){
                remove.add(candidate);
            }
        }
        candidates.removeAll(remove);
    }


    public static TerminalWrapper getInstance() throws RuntimeException {
        return Holder.INSTANCE;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public LineReader getLineReader() {
        return lineReader;
    }

    public Completer getCompleter() {
        return completer;
    }

    private static class Holder {
        static final TerminalWrapper INSTANCE;

        static {
            try {
                INSTANCE = new TerminalWrapper();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
