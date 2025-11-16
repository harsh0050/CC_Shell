public class Util {
    public static Result validateArgumentCount(int expected, int found) {
        if(found > expected) return new Failure("too many arguments");
        if(found < expected) return new Failure("too few arguments");
        return new Success();
    }
}
