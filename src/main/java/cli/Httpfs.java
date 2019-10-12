package cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Command(name = "httpfs",
        version = "1.0.0",
        description = "httpfs is a simple file server.",
        subcommands = HelpCommand.class)
public class Httpfs implements Runnable {
    @Spec
    private CommandSpec spec;

    @Option(names = {"-v", "--verbose"}, description = "Prints debugging messages")
    boolean verbose;

    @Option(names = {"-p", "--port"}, description = "Specifies the port number that the server will listen and serve at.", defaultValue = "8080")
    int port;

    @Option(
            names = {"-d", "--directory"},
            description = "Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the application."
    ) String directory = System.getProperty("user.dir");

    public Httpfs() {
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Httpfs()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println(verbose);
        System.out.println(port);
        System.out.println(directory);
    }
}
