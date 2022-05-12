package lslauncher;

import ls.LS;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;


public class LSLauncher {

    @Option(name = "-l", usage = "Switches output to long format")
    private boolean longOption = false;

    @Option(name = "-h", usage = "Switches output to human-readable format")
    private boolean humanReadableOption = false;

    @Option(name = "-r", usage = "Changes the output order to the opposite")
    private boolean reverseOption = false;

    @Option(name = "-o", metaVar = "OutputFile", usage = "Specifies the name of the file to output the result to")
    private String outputOption = null;

    //  Придумать значение по умолчанию (the current directory by default)
    //  (required = true)
    @Argument(metaVar = "DirectoryOrFile", usage = "Directory or file to list the content")
    private String dirOrFileName = ".";


    public static void main(String[] args) {
        new LSLauncher().launch(args);
    }

    private void launch(String[] args) {
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
            LS ls = new LS(longOption, humanReadableOption,
                    reverseOption, outputOption, dirOrFileName);
            ls.printDirOrFileContent();
        } catch (IOException e) {
            //System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}