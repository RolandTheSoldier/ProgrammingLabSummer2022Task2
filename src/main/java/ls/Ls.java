package ls;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Locale;

public class Ls {

    // Meaning of these "indent variables"
    /*

    These 4 required for printLongFormat(),
    exactly for proper indents and spacess

    "maxNumberOfDigitsOfFileSize" store max length of files sizes,
    e.g. with sizes 666, 444444, 3333 the longest will be 6 (444444)

    "maxLettersOFLastModifiedTime" is required for proper indents in
    print of <last modified time> option, folder <Fallout> for example:
                            ||
                            ||
       here used            ||
     first variable         ||
           ||               ||
           ||               ||           here the
           \/               \/            spaces
    rwx    166K    2017-05-15T07:40:40Z            atimgpud.dll
    rwx    339K    2017-05-15T07:40:40Z            binkw32.dll
    rwx       3    2022-01-24T23:28:57.20293Z      check.ini
    rwx    107K    2013-08-04T07:53:34.2276312Z    d3d9.dll
    rwx     13K    2022-01-31T19:06:20.4772961Z    Data
    rwx     70K    2017-11-29T14:57:59Z            EULA.txt
    rwx       0    2022-01-24T22:20:15.2204128Z    ExitData.mhd
    rwx     17M    2022-01-24T22:12:57.5389599Z    FalloutNV.exe

    Also, formatterSize and formatterTime are strings for formatting
    Sizes and Last modified times with <String.format()>

    */
    private int maxNumberOfDigitsOfFileSize = 0;
    private int maxLettersOFLastModifiedTime = 0;

    String formatterSize = "%1s";
    String formatterTime = "%-1s";

    private ArrayList<Double> listOfSizesDouble = null;
    private ArrayList<String> listOfSizesString = null;

    /*
          ^                                      ^
          | Additional variables and collections |
          \______________________________________/
    */

    private final boolean longOption;
    private final boolean humanReadableOption;
    private final boolean reverseOption;
    private final Path outputOption;
    private final Path dirOrFileName;


    /*
    Constuctor
    */
    public Ls(boolean longOption, boolean humanReadableOption,
              boolean reverseOption, String outputOption,
              String dirOrFileName) {

        this.longOption = longOption;
        this.humanReadableOption = humanReadableOption;
        this.reverseOption = reverseOption;
        this.outputOption = (outputOption != null)
                ? Path.of(outputOption) : null;
        this.dirOrFileName = (dirOrFileName != null)
                ? Path.of(dirOrFileName) : null;
    }


    /*
    Test method to provide your own sizes of files (50G, 888E, 6.3T)
    */
    public void setListOfSizesDouble(ArrayList<Double> listOfSizesDouble) {
        this.listOfSizesDouble = listOfSizesDouble;
        for (double size : listOfSizesDouble) {
            maxNumberOfDigitsOfFileSize = Math.max(maxNumberOfDigitsOfFileSize,
                    String.valueOf(size).length());
        }
    }


    /*
    Central method that checks if paths to print and to output are proper
    and determines which method should be called, uses longOption -l and
    outputOption -o output.file
    */
    public String returnOrOutputContent() throws IOException {


        if (!Files.exists(dirOrFileName)) {
            throw new IOException("Path to print doesn't exists.");
        }

        if (outputOption != null) {
            if (!Files.exists(outputOption)) {
                try {
                    Files.createFile(outputOption);
                } catch (IOException e) {
                    throw new IOException("Path to output file is unavailable: "
                            + outputOption + ".");
                }
            }
            if (!Files.isWritable(outputOption)) {
                throw new IOException(
                        "Access for writting to output file is denied.");
            }
        }

        String contentToPrint = !longOption
                ? getOnlyNames() : getLongFormat();


        if (outputOption != null) {
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(outputOption.toString()))) {
                writer.write(contentToPrint);
            }
            return "";
        }
        return contentToPrint;
    }


    /*
    Gives only names, uses reverseOption -r.
    */
    String getOnlyNames() throws IOException {
        StringBuilder pathContentString = new StringBuilder();
        if (!Files.isDirectory(dirOrFileName)) {
            pathContentString.append(dirOrFileName.getFileName())
                    .append(System.lineSeparator());
        } else {
            try (DirectoryStream<Path> listOfContent
                         = Files.newDirectoryStream(dirOrFileName)) {
                for (Path path : listOfContent) {
                    if (reverseOption) {
                        pathContentString.insert(
                                0, path.getFileName()
                                        + System.lineSeparator());
                    } else {
                        pathContentString.append(
                                        path.getFileName())
                                .append(System.lineSeparator());
                    }
                }
            }
        }
        return pathContentString.toString();
    }


    /*
    Prints content of directory or file information
    calling getFileLongOption(), uses reverseOption -r.
    */
    String getLongFormat() throws IOException {

        StringBuilder stringContent = new StringBuilder();
        boolean isListOfSizesNull = listOfSizesDouble == null;
        if (isListOfSizesNull) {
            listOfSizesDouble = new ArrayList<>();
        }

        if (!Files.isDirectory(dirOrFileName)) {
            if (isListOfSizesNull) {
                listOfSizesDouble.add((double) Files.size(dirOrFileName));
            }
            if (humanReadableOption) {
                listOfSizesString = new ArrayList<>();
                formatSizesIfHumanReadableOption();
            }
            stringContent.append(getFileLongOption(dirOrFileName));
        } else {
            try (DirectoryStream<Path> listOfContent
                         = Files.newDirectoryStream(dirOrFileName)) {
                for (Path path : listOfContent) {
                    if (isListOfSizesNull) {
                        listOfSizesDouble.add((double) Files.size(path));
                        maxNumberOfDigitsOfFileSize = Math.max(
                                maxNumberOfDigitsOfFileSize,
                                String.valueOf(Files.size(path))
                                        .length());
                    }
                    maxLettersOFLastModifiedTime = Math.max(
                            Files.getLastModifiedTime(path).toString().length(),
                            maxLettersOFLastModifiedTime);
                }
            }

            if (humanReadableOption) {
                maxNumberOfDigitsOfFileSize = 0;
                listOfSizesString = new ArrayList<>();
                formatSizesIfHumanReadableOption();
            }
            formatterSize = formatterSize.replaceFirst("1",
                    String.valueOf(maxNumberOfDigitsOfFileSize));
            formatterTime = formatterTime.replaceFirst("1",
                    String.valueOf(maxLettersOFLastModifiedTime));

            try (DirectoryStream<Path> listOfContent
                         = Files.newDirectoryStream(dirOrFileName)) {
                for (Path path : listOfContent) {
                    if (reverseOption) {
                        stringContent.insert(0, getFileLongOption(path));
                    } else {
                        stringContent.append(getFileLongOption(path));
                    }
                }
            }
        }
        return stringContent.toString();
    }


    /*
    Method that traslates sizes of files to human-readable format
    <Locale.US> is used to format double values properly:
    With dots - 6.5K instead of commas - 4,9M
    */
    void formatSizesIfHumanReadableOption() {
        for (double sizeOfFile : listOfSizesDouble) {
            double sizeDouble = sizeOfFile;
            int numberOfTripleDigits = 0;
            while (sizeDouble / 1000 >= 1) {
                ++numberOfTripleDigits;
                sizeDouble /= 1000;
            }

            String sizeString;
            int sizeInt = (int) sizeDouble;
            int lengthOfInt = String.valueOf(sizeInt).length();
            int lengthOfFormatted = (sizeString
                    = String.format(Locale.US,
                    "%.0f", sizeDouble)).length();

            if (lengthOfFormatted == 4) {
                ++numberOfTripleDigits;
                sizeDouble /= 1000.;
                sizeString = String.format(Locale.US,
                        "%.1f", sizeDouble);
                maxNumberOfDigitsOfFileSize = 4;
            }

            if (numberOfTripleDigits > 0) {
                if (lengthOfFormatted == 1) {
                    sizeString = String.format(Locale.US,
                            "%.1f", sizeDouble);
                }
                if (lengthOfFormatted == 2) {
                    ++lengthOfFormatted;
                } else {
                    maxNumberOfDigitsOfFileSize = 4;
                }
            }

            maxNumberOfDigitsOfFileSize = Math.max(lengthOfFormatted,
                    maxNumberOfDigitsOfFileSize);

            switch (numberOfTripleDigits) {
                case 1 -> sizeString += "K";
                case 2 -> sizeString += "M";
                case 3 -> sizeString += "G";
                case 4 -> sizeString += "T";
                case 5 -> sizeString += "P";
                case 6 -> sizeString += "E";
            }

            listOfSizesString.add(sizeString);

        }
    }


    /*
    Uses when there is call from printLongFormat().
    Gives full information about file, dir or symlink including
    name, access permissions, last time modified, uses humanReadableOption -h
    */
    String getFileLongOption(Path filePath) throws IOException {

        StringBuilder fileInfo = new StringBuilder();

        /*
        Code considering whether humanReadableOption is <true> or <false>
        Then calculating and appeding AccessPermissions, Size,
        LastModifiedTime and Name
        */

        if (humanReadableOption) {

            fileInfo.append(Files.isReadable(filePath) ? "r" : "-")
                    .append(Files.isWritable(filePath) ? "w" : "-")
                    .append(Files.isExecutable(filePath) ? "x" : "-")
                    .append("    ");

            String size = listOfSizesString.get(0);
            listOfSizesString.remove(0);
            fileInfo.append(String.format(formatterSize, size));

        } else {

            byte rwx = 0;
            if (Files.isReadable(filePath)) rwx += 100;
            if (Files.isWritable(filePath)) rwx += 10;
            if (Files.isExecutable(filePath)) rwx += 1;
            fileInfo.append(String.format("%03d", rwx)).append("    ");

            String size = String.valueOf(Math.round(listOfSizesDouble.get(0)));
            listOfSizesDouble.remove(0);
            fileInfo.append(String.format(formatterSize, size));

        }

        fileInfo.append("    ");
        fileInfo.append(String.format(Locale.US, formatterTime,
                Files.getLastModifiedTime(filePath))).append("    ");
        fileInfo.append(filePath.getFileName()).append(System.lineSeparator());

        return fileInfo.toString();
    }


}
