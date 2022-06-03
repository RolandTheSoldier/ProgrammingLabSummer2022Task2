package ls;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Ls {

    // Meaning of these 3 "indent variables"
    /*

    These 3 required for printLongFormat(),
    exactly for proper indents and spacess

    Truth of "areThereAnyNonTwoDigitNumbers" means that there
    are non-double digit numbers as 45K, 88M or 99G

    "maxNumberOfDigitsOfFileSize" store max length of files sizes,
    e.g. with sizes 666, 444444, 3333 the longest will be 6 (444444)

    "maxLettersOFLastModifiedTime" is required for proper indents in
    print of <last modified time> option, folder <Fallout> for example:
                            ||
       here used            ||
    first and second        ||
       variables            ||
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

    */
    private boolean areThereAnyNonDoubleDigitNumbers;
    private int maxNumberOfDigitsOfFileSize;
    private int maxLettersOFLastModifiedTime;

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
        this.outputOption = (outputOption == null)
                ? null : Path.of(outputOption);
        this.dirOrFileName = (dirOrFileName == null)
                ? null : Path.of(dirOrFileName);
    }


    /*
    Central method that checks if paths to print and to output are proper
    and determines which method should be called
    */
    public String printDirOrFileContent() throws IOException {

        /*
        There are three conditions down here to check out the existence
        and accessibility of dirOrFileName and outputOption
        */
        // #1
        if (!Files.exists(dirOrFileName)) {
            throw new IOException("Path to print doesn't exists.");
        }

        // #2
        if (!Files.isDirectory(dirOrFileName)
                && !Files.isRegularFile(dirOrFileName)
                && !Files.isSymbolicLink(dirOrFileName)) {
            throw new IOException("Path to print is not a Directory," +
                    " RegularFile or SymbolicLink.");
        }

        // #3
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



        /*
        Final string with print information
        */
        String contentToPrint;

        /*
        Choosing the right method to call by considering value of longOption
        */
        if (!longOption) {
            contentToPrint = printOnlyNames();
        } else {
            contentToPrint = printLongFormat();
        }

        /*
        Checking option -o.
        If it is null then will print to console
        Otherwise will print to file by path of outputOption
        */
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
    Prints only names of files and directories, uses reverseOption -r.
    */
    String printOnlyNames() throws IOException {
        StringBuilder pathContentString = new StringBuilder();

        /*
        True: prints name of file or symlink
        False: prints names of files and folders in directory
        */
        if (!Files.isDirectory(dirOrFileName)) {
            pathContentString.append(dirOrFileName.getFileName()).append("\n");
        } else {
            try (DirectoryStream<Path> listOfContent
                         = Files.newDirectoryStream(dirOrFileName)) {
                if (reverseOption) {
                    for (Path path : listOfContent) {
                        pathContentString.insert(0, path.getFileName() + "\n");
                    }
                } else {
                    for (Path path : listOfContent) {
                        pathContentString.append(path.getFileName()).append("\n");
                    }
                }
            }
        }
        return pathContentString.toString();
    }


    /*
    Uses when there is call from printLongFormat().
    Gives full information about file, dir or symlink including
    name, access permissions, last time modifi
    */
    String printFileLongOption(Path filePath) throws IOException {

        /*
        "file" in "fileInfoString" means Directory, RegularFile or SymbolicLink
        */
        StringBuilder fileInfoString = new StringBuilder();

        /*
        Code considering whether humanReadableOption is <true> or <false>
        Then calculating and appeding Size and LastModifiedTime
        */
        if (humanReadableOption) {

            /*
            Access permissions: rwx
            */
            if (Files.isReadable(filePath)) fileInfoString.append("r");
            else fileInfoString.append("-");
            if (Files.isWritable(filePath)) fileInfoString.append("w");
            else fileInfoString.append("-");
            if (Files.isExecutable(filePath)) fileInfoString.append("x");
            else fileInfoString.append("-");
            fileInfoString.append("    ");

            /*
                        ---- START APPENDING SIZE ----
            */

            double sizeOfFileDouble = (double) Files.size(filePath);
            int i = 0;
            while (sizeOfFileDouble / 1000 >= 1) {
                sizeOfFileDouble /= 1000;
                ++i;
            }

            /*
            Rounded size
            */
            Integer sizeOfFileInt = (int) Math.ceil(sizeOfFileDouble);

            /*
            <if> for bytes (0 - 999), <else> for multiples of
            thousands (1K and more)
            */
            if (i == 0) {
                fileInfoString.append(" ".repeat(Math.max(0,
                        Math.min(maxNumberOfDigitsOfFileSize, 4)
                                - sizeOfFileInt.toString().length())));
                fileInfoString.append(sizeOfFileInt);
            } else {

                /*
                This BigDecimal for numbers like 1.4K and 9.8G.
                Won't use if number has different format, for example 9 or 85G
                */
                BigDecimal sizeOfFileWithCeiling = (sizeOfFileDouble < 10)
                        ? BigDecimal.valueOf(sizeOfFileDouble)
                        .setScale(1, RoundingMode.CEILING) : null;

                switch (sizeOfFileInt.toString().length()) {
                    case 1 -> fileInfoString.append(sizeOfFileWithCeiling);
                    case 2 -> {
                        if (areThereAnyNonDoubleDigitNumbers) {
                            fileInfoString.append(" ");
                        }
                        fileInfoString.append(sizeOfFileInt);
                    }
                    case 3 -> fileInfoString.append(sizeOfFileInt);
                }

                /*
                Appending postfix meaning the measurement
                */
                switch (i) {
                    case 1 -> fileInfoString.append("K");
                    case 2 -> fileInfoString.append("M");
                    case 3 -> fileInfoString.append("G");
                    case 4 -> fileInfoString.append("T");
                    case 5 -> fileInfoString.append("P");
                    case 6 -> fileInfoString.append("E");
                }
                /*
                            ---- END APPENDING SIZE ----
                */
            }

            /*
             * * *   humanReadabilityOption is <false>:  * * *
             */
        } else {

            /*
            Access permissions: binary 000 - 111
            */
            byte rwx = 0b0;
            if (Files.isReadable(filePath)) rwx += 0b100;
            if (Files.isWritable(filePath)) rwx += 0b10;
            if (Files.isExecutable(filePath)) rwx += 0b1;
            if (rwx == 0) fileInfoString.append("00");
            if (rwx == 2 || rwx == 3) fileInfoString.append("0");
            fileInfoString.append(Integer.toBinaryString(rwx)).append("    ");

            /*
            Sizes in bytes
            */
            long sizeOfFileLong = Files.size(filePath);
            int rowNumberOfDigitsOfFileSize = Integer
                    .toString((int) sizeOfFileLong).length();
            rowNumberOfDigitsOfFileSize = maxNumberOfDigitsOfFileSize
                    - rowNumberOfDigitsOfFileSize;
            fileInfoString.append(" ".repeat(Math.max(0,
                    rowNumberOfDigitsOfFileSize)));
            fileInfoString.append(sizeOfFileLong);
        }

        fileInfoString.append("    ");

        /*
        Last time modification
        */
        fileInfoString.append(Files.getLastModifiedTime(filePath))
                .append("    ");
        fileInfoString.append(" ".repeat(
                Math.max(0, maxLettersOFLastModifiedTime
                        - Files.getLastModifiedTime(filePath)
                        .toString().length())));

        /*
        Names of files
        */
        fileInfoString.append(filePath.getFileName()).append("\n");

        return fileInfoString.toString();
    }


    /*
    Prints content of directory or information of file or symlink
    using longOption and calling printFileLongOption().
    Uses reverseOption -r.
    */
    String printLongFormat() throws IOException {

        StringBuilder stringContent = new StringBuilder();

        /*
        True: prints name of file or symlink
        False: prints names of files and folders in directory
        Variable maxNumberOfDigitsOfFileSize is used
        */
        if (!Files.isDirectory(dirOrFileName)) {
            stringContent.append(printFileLongOption(dirOrFileName));
        } else {

            /*
            There are different Streams and Try-with-resources constructions
            because Iterator of DirectoryStream can be called only once

            The first one: For counting and definiting
            of three "indent variables"
            */
            try (DirectoryStream<Path> listOfContent
                         = Files.newDirectoryStream(dirOrFileName)) {
                long maxSize = 0;
                for (Path path : listOfContent) {
                    maxSize = Math.max(Files.size(path), maxSize);
                    if (Files.size(path) >= 1000
                            && (Long.toString(Files.size(path))
                            .length() - 2) % 3 != 0) {
                        areThereAnyNonDoubleDigitNumbers = true;
                    }
                    maxLettersOFLastModifiedTime = Math.max(
                            Files.getLastModifiedTime(path).toString().length(),
                            maxLettersOFLastModifiedTime);
                }
                if (humanReadableOption && !areThereAnyNonDoubleDigitNumbers) {
                    maxNumberOfDigitsOfFileSize = Math.min(3,
                            Long.toString(maxSize).length());
                } else {
                    maxNumberOfDigitsOfFileSize
                            = Long.toString(maxSize).length();
                }
            }

            /*
            The second one: For appending and inserting
            files information from directory
            in the final String version of LS
            */
            try (DirectoryStream<Path> listOfContent
                         = Files.newDirectoryStream(dirOrFileName)) {
                if (reverseOption) {
                    for (Path path : listOfContent) {
                        stringContent.insert(0, printFileLongOption(path));
                    }
                } else {
                    for (Path path : listOfContent) {
                        stringContent.append(printFileLongOption(path));
                    }
                }
            }
        }
        return stringContent.toString();
    }


}
