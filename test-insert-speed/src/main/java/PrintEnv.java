import java.io.IOException;

public class PrintEnv {
	public static void main(String[] args) throws IOException {
		System.getProperties().store(System.out, "user system properties.");
	}
}
