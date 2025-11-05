public class Util {
    public static void validateArgumentCount(int expected, int found) throws Exception {
        if(found > expected) throw new Exception("too many arguments");
        if(found < expected) throw new Exception("too few arguments");
    }
}
