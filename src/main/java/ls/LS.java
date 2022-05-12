package ls;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class LS {

    private boolean longOption = false;
    private boolean humanReadableOption = false;
    private boolean reverseOption = false;
    private String outputOption = null;
    private String dirOrFileName = null;



    public LS(boolean longOption, boolean humanReadableOption, boolean reverseOption,
              String outputOption, String dirOrFileName) {
        this.longOption = longOption;
        this.humanReadableOption = humanReadableOption;
        this.reverseOption = reverseOption;
        this.outputOption = outputOption;
        this.dirOrFileName = dirOrFileName;
    }



    public void printDirOrFileContent() throws IOException {
        Path pathToPrint;
        Path pathToOutput = null;
        try {
            pathToPrint = Path.of(dirOrFileName);
            if (!Files.exists(pathToPrint)
                    || (!Files.isDirectory(pathToPrint)
                    && !Files.isRegularFile(pathToPrint))) {
                throw new IOException("Path to print doesn't exists.");
            }
        } catch (NullPointerException e) {
            throw new IOException("Invalid path value to print.");
        }

        if (outputOption != null) {
            try {
                pathToOutput = Path.of(outputOption);
                for (int i = 1; ; ++i) {
                    if (!Files.exists(Path.of(
                            pathToOutput + "/lsOutputLog" + i + ".txt"))) {
                        Files.createFile(Path.of(
                                pathToOutput + "/lsOutputLog" + i + ".txt"));
                        break;
                    }
                }
            } catch (IOException e) {
                throw new IOException("Can not to output along this path.");
            }
        }

        if (Files.isRegularFile(pathToPrint)) {
            printFileInfo(pathToPrint, pathToOutput);
        } else if (longOption) {
            printLongFormat(pathToPrint, pathToOutput);
        } else {
            printOnlyNames(pathToPrint, pathToOutput);
        }
    }



    void printFileInfo(Path pathToPrint, Path pathToOutput)
            throws IOException {
        StringBuilder stringContent = new StringBuilder();
        byte rwx = 0b0;
        if (Files.isReadable(pathToPrint)) rwx += 0b100;
        if (Files.isWritable(pathToPrint)) rwx += 0b10;
        if (Files.isExecutable(pathToPrint)) rwx += 0b1;
        stringContent.append(pathToPrint.getFileName()).append("    ");
        stringContent.append(Integer.toBinaryString(rwx)).append("    ");
        stringContent.append(Files.getLastModifiedTime(pathToPrint)).append("    ");
        stringContent.append(Files.size(pathToPrint));
        if (pathToOutput == null) {
            System.out.println(stringContent);
        } else {
            Files.writeString(pathToOutput, stringContent);
        }
    }



    void printOnlyNames(Path pathToPrint, Path pathToOutput)
            throws IOException {
        StringBuilder stringContent = new StringBuilder();
        try (DirectoryStream<Path> listOfContent
                     = Files.newDirectoryStream(pathToPrint)) {
            for (Path path : listOfContent) {
                // information reading
            }
        }
        // return stringContent.toString(); - has to be deleted
    }



    void printLongFormat(Path pathToPrint, Path pathToOutput)
            throws IOException {
        StringBuilder stringContent = new StringBuilder();
        try (DirectoryStream<Path> listOfContent
                     = Files.newDirectoryStream(pathToPrint)) {
            for (Path path : listOfContent) {
                // information reading
            }
        }
        // return stringContent.toString(); - has to be deleted
    }


}
