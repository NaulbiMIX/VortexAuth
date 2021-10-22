package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class cmd implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public cmd(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }
    boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");

    Process process;
    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        String homeDirectory = System.getProperty("user.home");
        if (isWindows) {
            try {
                process = Runtime.getRuntime()
                        .exec(String.format("cmd.exe /c dir %s", homeDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                process = Runtime.getRuntime()
                        .exec(String.format("sh -c ls %s", homeDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cmd streamGobbler =
                new cmd(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert exitCode == 0;
    }
}
