import java.util.LinkedList;
import java.util.List;

public class Command {
    private final String[] argv;

    public Command(String command) {
        argv = getArgv(command);
    }

    public int run() {
        if(argv.length == 0) return 0;
        try{
            Executable exec = BuiltIn.fromCommand(argv[0]);
            if (exec == null) {
                exec = new ExternalProgram(argv[0], Navigation.getWorkingDir());
            }
            exec.execute(argv);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }


    private static String[] getArgv(String command) {
        List<String> argv = new LinkedList<>();
        //todo 'quote>' functionality
        getArgv(command, 0, argv);
        return argv.toArray(new String[0]);
    }

    /**
     * @throws IllegalArgumentException if there is syntax error in provided command arguments
     */
    private static void getArgv(String command, int start, List<String> ls) throws IllegalArgumentException {
        if (start == command.length()) return;
        boolean singleQuoteOpen = false;
        boolean doubleQuoteOpen = false;
        int i = start;
        StringBuilder sb = new StringBuilder();
        while (i < command.length()) {
            char currChar = command.charAt(i++);
            if (currChar == '\'' && !doubleQuoteOpen) {
                singleQuoteOpen = !singleQuoteOpen;
                continue;
            }
            if (currChar == '\"' && !singleQuoteOpen) {
                doubleQuoteOpen = !doubleQuoteOpen;
                continue;
            }
            if (currChar == ' ' && !(singleQuoteOpen || doubleQuoteOpen)) {
                if (!sb.isEmpty()) ls.add(sb.toString());
                getArgv(command, i, ls);
                return;
            }
            if (currChar == '\\' && !singleQuoteOpen && !doubleQuoteOpen) { // outside of single and double quotes (escape)
                if (i >= command.length()) {
                    throw new IllegalArgumentException("Expected the escaped character at the end");
                }
                currChar = command.charAt(i++);
            }
            if (currChar == '\\' && doubleQuoteOpen) { //inside of double quotes (conditional escape)
                if (i >= command.length()) {
                    throw new IllegalArgumentException("Expected the escaped character at the end");
                }
                currChar = switch (command.charAt(i)) {
                    case '"', '\\' -> command.charAt(i++);
                    default -> currChar;
                };
            }
            sb.append(currChar);
        }
        if (!sb.isEmpty()) ls.add(sb.toString());
    }

}
