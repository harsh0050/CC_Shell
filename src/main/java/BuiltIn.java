
public enum BuiltIn {
    ECHO("echo"), EXIT("exit"), TYPE("type"), PWD("pwd"), CD("cd");
    public final String command;

    BuiltIn(String command) {
        this.command = command;
    }

    /**@return Enum of the given command if it exists, otherwise {@code null}*/
    public static BuiltIn fromCommand(String command){
        for(BuiltIn b: values()){
            if(b.command.equals(command)){
                return b;
            }
        }
        return null;
    }
}

