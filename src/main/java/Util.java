public class Util {
    public static Result validateArgumentCount(int expected, int found) {
        if(found > expected) return new Failure(Constants.TOO_MANY_ARGUMENTS);
        if(found < expected) return new Failure(Constants.TOO_FEW_ARGUMENTS);
        return new Success();
    }
}
