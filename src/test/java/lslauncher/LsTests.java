package lslauncher;

import ls.Ls;

import java.io.*;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tools.ant.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LsTests {


    String pathStr1 = new String("d:/");
    String[] args = new String[]{pathStr1};

    /*
    Method for getting content of file to compare with result of print later.
    */
    String resultOfPrint(String strPath) throws IOException {
        // Files.readAllLines() // ВОТ ОН
        try (BufferedReader reader = new BufferedReader(
                new FileReader(strPath))) {
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str).append("\n");
            }
            return stringBuilder.toString();
        }
    }


    @Test
    void printDirOrFileContentTest() throws IOException {


        /*
        The "/src/test/inputfiles" is required path that have to be
        in project directory for tests
        */
        String resources = "src/test/inputfiles";


        assertEquals(resultOfPrint(resources + "/test1_l.txt"),
                new Ls(true, false,
                        false, null, "d:/")
                        .printDirOrFileContent());
        assertEquals(resultOfPrint(resources + "/test1_lr.txt"),
                new Ls(true, false,
                        true, null, "d:/")
                        .printDirOrFileContent());
        assertEquals(resultOfPrint(resources + "/test1_lh.txt"),
                new Ls(true, true,
                        false, null, "d:/")
                        .printDirOrFileContent());
        assertEquals(resultOfPrint(resources + "/test1_lhr.txt"),
                new Ls(true, true,
                        true, null, "d:/")
                        .printDirOrFileContent());


        new Ls(true, true, true,
                resources + "/test1_lhro.txt", "d:/")
                .printDirOrFileContent();
        assertEquals(resultOfPrint(resources + "/test1_lhr.txt"),
                resultOfPrint(resources + "/test1_lhro.txt"));
        Files.deleteIfExists(Path.of(resources + "/test1_lhro.txt"));


    }


}
