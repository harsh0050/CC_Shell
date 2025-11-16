public sealed class Result permits Success, Failure{
    int statusCode;

    public Result(int statusCode) {
        this.statusCode = statusCode;
    }
}
final class Success extends Result {
    public Success() {
        super(0);
    }
}
final class Failure extends Result {
    String message;

    public Failure(String message) {
        super(1);
        this.message = message;
    }
}
