import java.util.LinkedList;
import java.util.List;

public class Util {

    public static String[] getArgv(String command){
        List<String> argv = new LinkedList<>();
        getArgv(command, 0, argv);
        return argv.toArray(new String[0]);
    }
    private static void getArgv(String command, int start, List<String> ls) {
        if (start == command.length()) return;
        boolean quoteOpen = false;
        int i = start;
        StringBuilder sb = new StringBuilder();
        while (i < command.length()) {
            char currChar = command.charAt(i++);
            if (currChar == '\'') {
                quoteOpen = !quoteOpen;
                continue;
            }
            if (quoteOpen || currChar != ' ') {
                sb.append(currChar);
            }
            if (currChar == ' ' && !quoteOpen) {
                if (!sb.isEmpty()) ls.add(sb.toString());
                getArgv(command, i, ls);
                return;
            }
        }
        if(i == command.length() && !quoteOpen) ls.add(sb.toString());
    }
    public static Result validateArgumentCount(int expected, int found) {
        if(found > expected) return new Failure(Constants.TOO_MANY_ARGUMENTS);
        if(found < expected) return new Failure(Constants.TOO_FEW_ARGUMENTS);
        return new Success();
    }
}
