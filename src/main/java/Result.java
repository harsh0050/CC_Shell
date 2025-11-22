public abstract sealed class Result permits Failure, Pending, Success {
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


final class Pending extends Result {
    private final Process process;

    public Pending(Process task) {
        super(-1);
        this.process = task;
    }

    public void resolve() throws InterruptedException {
        this.statusCode = process.waitFor();
    }
}

final class Failure extends Result {
    String message;

    public Failure(String message) {
        super(1);
        this.message = message;
    }
}

