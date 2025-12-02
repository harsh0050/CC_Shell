import java.io.IOException;

public class OutputTest {
    static void main() throws IOException, InterruptedException {
        //Change buffered writer to outputstreams
//        new ProcessBuilder("codecrafters", "test").inheritIO().start().waitFor();
//        OutputStreamWriter bf = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
//        bf.write(226);
//        bf.write(156);
//        bf.write(147);
        System.out.println(new String(new char[]{'a','\0','b'}));

//        bf.flush();
    }
}
