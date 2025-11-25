public interface Executable {

    /**
     * @return {@code 0} if execution was successful, otherwise a non-zero value.
     */
    int execute(String... argv);
}
