package lslauncher;

import ls.Ls;

import java.io.IOException;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


public class LsLauncher {

    @Option(name = "-l", usage = "Switches output to long format")
    private boolean longOption = false;

    @Option(name = "-h", usage = "Switches output to human-readable format")
    private boolean humanReadableOption = false;

    @Option(name = "-r", usage = "Changes the output order to the opposite")
    private boolean reverseOption = false;

    @Option(name = "-o", metaVar = "OutputFile", usage = "Specifies the name of the file to output the result to")
    private String outputOption = null;

    @Argument(metaVar = "DirectoryOrFile", usage = "Directory or file to list the content")
    private String dirOrFileName = ".";


    public static void main(String[] args) {
        new LsLauncher().launch(args);
    }

    void launch(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar ls.jar");
            parser.printUsage(System.err);
            return;
        }

        try {
            Ls ls = new Ls(longOption, humanReadableOption,
                    reverseOption, outputOption, dirOrFileName);
            System.out.println(ls.returnOrOutputContent());
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}