package cli;

import http.HttpServer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.IOException;

@Command(name = "httpfs",
        version = "1.0.0",
        description = "httpfs is a simple file server.",
        subcommands = HelpCommand.class)
public class Httpfs implements Runnable {
    @Spec
    private CommandSpec spec;

    private HttpServer httpServer;

    @Option(names = {"-v", "--verbose"}, description = "Prints debugging messages")
    boolean verbose;

    @Option(names = {"-p", "--port"}, description = "Specifies the port number that the server will listen and serve at.")
    int port = 8080;

    @Option(
            names = {"-d", "--directory"},
            description = "Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the application."
    ) String directory = System.getProperty("user.dir");

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Httpfs()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            this.httpServer = new HttpServer(this.port);
            this.httpServer.setDebug(verbose);
            this.httpServer.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
