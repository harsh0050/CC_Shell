import org.jline.builtins.Completers;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.EnumCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class ShellLineReader {
    private final LineReader lineReader;
    private final Completer completer;
    private final Terminal terminal;

    public ShellLineReader() throws IOException {
        this.terminal = TerminalBuilder.builder().system(true).build();

        this.lineReader = LineReaderBuilder.builder()
                .parser(new DefaultParser().escapeChars(null))
                .terminal(terminal)
                .history(ShellHistory.getInstance())
                .option(LineReader.Option.HISTORY_IGNORE_DUPS, false)
                .option(LineReader.Option.HISTORY_TIMESTAMPED, false)
                .variable(LineReader.HISTORY_FILE, Path.of(Constants.HISTORY_FILE_PATH))
                .build();

        lineReader.getKeyMaps().get(LineReader.MAIN).bind(suggestionWidget(), "\t");


        LinkedList<Completer> completers = new LinkedList<>();
        completers.add(new EnumCompleter(BuiltIn.class));

        String allPaths = System.getenv(Constants.PATH_ENV_IDENTIFIER);
        if (allPaths != null) {
            for (String path : ExternalProgram.getValidDirPaths(allPaths)) {
                completers.add(new Completers.FilesCompleter(Path.of(path)));
            }
        }
        this.completer = new AggregateCompleter(completers);
    }

    public String readLine(String prefix) {
        return lineReader.readLine(prefix);
    }

    private static void filterList(List<Candidate> candidates, String filter) {
        List<Candidate> remove = new LinkedList<>();
        for (Candidate candidate : candidates) {
            if (!candidate.value().startsWith(filter)) {
                remove.add(candidate);
            }
        }
        candidates.removeAll(remove);
    }

    private static String joinList(List<Candidate> candidates, String delimiter) {
        if (candidates.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Candidate candidate : candidates) {
            sb.append(candidate.value());
            sb.append(delimiter);
        }
        sb.delete(sb.length() - delimiter.length(), sb.length());
        return sb.toString();
    }

    private Widget suggestionWidget() {
        return new Widget() {
            int tabCount = 0;

            @Override
            public boolean apply() {
                tabCount++;
                String currentLine = lineReader.getBuffer().toString();
                ParsedLine parsedLine = lineReader.getParser().parse(currentLine, currentLine.length());

                List<Candidate> candidates = new ArrayList<>();
                completer.complete(lineReader, parsedLine, candidates);
                filterList(candidates, parsedLine.word());
                candidates = new ArrayList<>(new TreeSet<>(candidates)); //remove duplicates

                if (candidates.isEmpty()) {
                    terminal.puts(InfoCmp.Capability.bell);
                    terminal.flush();
                    return true;
                }
                if (candidates.size() == 1) {
                    String append = candidates.getFirst().value().substring(parsedLine.word().length());
                    lineReader.getBuffer().write(append + " ");
                    return true;
                }
                if (tabCount == 1) {
                    terminal.puts(InfoCmp.Capability.bell);
                    terminal.flush();
                    return true;
                }

                terminal.writer().println("\n" + joinList(candidates, "  "));
                terminal.writer().write("$ " + currentLine);
                tabCount = 0;
                return true;
            }
        };
    }
}

