package lslauncher;

import ls.Ls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LsTests {


    /*
    Text files folders
    */
    String resources = "src/test/resources";
    String toPrint = resources + "/toprint";
    String toPrintArbitrarySizes = resources + "/toprintarbitrarysizes";
    String toOutput = resources + "/tooutput";
    String toOutputArbitrarySizes = resources + "/tooutputarbitrarysizes";
    String toCompareWith = resources + "/tocomparewith";


    /*
    Method for getting content of file to compare with result of print later.
    */
    String contentOf(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while (reader.ready()) {
                builder.append(reader.readLine()).append('\n');
            }
        }
        return builder.toString();
    }


    // --- No outputOption ---
    @Test
    void printTest() throws IOException {


        // No longerOption
        assertEquals(contentOf(toCompareWith + "/test1_.txt"),
                new Ls(false, false,
                        false, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));
        assertEquals(contentOf(toCompareWith + "/test1_r.txt"),
                new Ls(false, false,
                        true, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));
        assertEquals(contentOf(toCompareWith + "/test1_h.txt"),
                new Ls(false, true,
                        false, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));
        assertEquals(contentOf(toCompareWith + "/test1_hr.txt"),
                new Ls(false, true,
                        true, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));

        // With longerOption
        assertEquals(contentOf(toCompareWith + "/test1_l.txt"),
                new Ls(true, false,
                        false, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));
        assertEquals(contentOf(toCompareWith + "/test1_lr.txt"),
                new Ls(true, false,
                        true, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));
        assertEquals(contentOf(toCompareWith + "/test1_lh.txt"),
                new Ls(true, true,
                        false, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));
        assertEquals(contentOf(toCompareWith + "/test1_lhr.txt"),
                new Ls(true, true,
                        true, null, toPrint)
                        .returnOrOutputContent()
                        .replaceAll("\\r\\n", "\n"));


    }


    // --- With outputOption ---
    @Test
    void outputTest() throws IOException {


        // No longerOption
        new Ls(false, false, false,
                toOutput + "/test1_o.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_.txt"),
                contentOf(toOutput + "/test1_o.txt"));
        new Ls(false, false, true,
                toOutput + "/test1_ro.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_r.txt"),
                contentOf(toOutput + "/test1_ro.txt"));
        new Ls(false, true, false,
                toOutput + "/test1_ho.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_h.txt"),
                contentOf(toOutput + "/test1_ho.txt"));
        new Ls(false, true, true,
                toOutput + "/test1_hro.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_hr.txt"),
                contentOf(toOutput + "/test1_hro.txt"));


        // With longerOption
        new Ls(true, false, false,
                toOutput + "/test1_lo.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_l.txt"),
                contentOf(toOutput + "/test1_lo.txt"));
        new Ls(true, false, true,
                toOutput + "/test1_lro.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_lr.txt"),
                contentOf(toOutput + "/test1_lro.txt"));
        new Ls(true, true, false,
                toOutput + "/test1_lho.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_lh.txt"),
                contentOf(toOutput + "/test1_lho.txt"));
        new Ls(true, true, true,
                toOutput + "/test1_lhro.txt", toPrint)
                .returnOrOutputContent();
        assertEquals(contentOf(toCompareWith + "/test1_lhr.txt"),
                contentOf(toOutput + "/test1_lhro.txt"));


    }


    @Test
    void setSizesTest() throws IOException {


        ArrayList<Double> al = new ArrayList<>(
                List.of(3., 14., 159., 77889., 2038018., 393939393.,
                        12345678900., 88664422002244., 1029384756657483.,
                        99555999555999555999.)
        );


        Ls ls = new Ls(true, false,
                true, toOutputArbitrarySizes
                + "/testSizes1_lro.txt", toPrintArbitrarySizes
                + "/test5.txt");
        ls.setListOfSizesDouble(new ArrayList<>(List.of(0009876.54321)));
        ls.returnOrOutputContent();
        Ls ls2 = new Ls(true,
                false, true, null,
                toPrintArbitrarySizes + "/test5.txt");
        ls2.setListOfSizesDouble(new ArrayList<>(List.of(0009876.54321)));
        assertEquals(contentOf(toOutputArbitrarySizes
                + "/testSizes1_lro.txt"), ls2.returnOrOutputContent()
                .replaceAll("\\r\\n", "\n"));


        ls = new Ls(true, false,
                false, toOutputArbitrarySizes
                + "/testSizes1_lo.txt", toPrintArbitrarySizes
                + "/test3.txt");
        ls.returnOrOutputContent();
        assertEquals(contentOf(toOutputArbitrarySizes
                + "/testSizes1_lo.txt"), new Ls(true,
                false, false, null,
                toPrintArbitrarySizes + "/test3.txt")
                .returnOrOutputContent()
                .replaceAll("\\r\\n", "\n"));


        ls = new Ls(false, false,
                false,
                toOutputArbitrarySizes + "/testSizes1_o.txt",
                toPrintArbitrarySizes + "/test8.txt");
        ls.setListOfSizesDouble(al);
        ls.returnOrOutputContent();
        assertEquals(contentOf(toOutputArbitrarySizes
                + "/testSizes1_o.txt"), new Ls(false,
                false, false,
                null, toPrintArbitrarySizes
                + "/test8.txt").returnOrOutputContent()
                .replaceAll("\\r\\n", "\n"));


        ls = new Ls(true, true,
                false,
                toOutputArbitrarySizes + "/testSizes1_lho.txt",
                toPrintArbitrarySizes + "/test10.txt");
        ls.setListOfSizesDouble(new ArrayList<>(
                List.of(8_888_888_888_888_888_888.)));
        ls.returnOrOutputContent();
        ls2 = new Ls(true,
                true, false,
                null, toPrintArbitrarySizes
                + "/test10.txt");
        ls2.setListOfSizesDouble(new ArrayList<>(
                List.of(8_888_888_888_888_888_888.)));
        assertEquals(contentOf(toOutputArbitrarySizes
                + "/testSizes1_lho.txt"), ls2.returnOrOutputContent()
                .replaceAll("\\r\\n", "\n"));


        ls = new Ls(true, true, true,
                toOutputArbitrarySizes + "/testSizes1_lhro.txt",
                toPrintArbitrarySizes);
        ls.setListOfSizesDouble(al);
        ls.returnOrOutputContent();
        ls2 = new Ls(true, true, true,
                null, toPrintArbitrarySizes);
        ls2.setListOfSizesDouble(al);
        assertEquals(contentOf(toOutputArbitrarySizes
                + "/testSizes1_lhro.txt"), ls2.returnOrOutputContent()
                .replaceAll("\\r\\n", "\n"));


    }


    @Test
    void throwTest() throws IOException {


        Ls ls;
        ls = new Ls(true, true, true,
                null, toPrint
                + "/nonexistent/test.txt");
        assertThrows(IOException.class, ls::returnOrOutputContent);

        ls = new Ls(true, true, true,
                toOutput + "/nonexistent/output.txt",
                toPrint + "test1.txt");
        assertThrows(IOException.class, ls::returnOrOutputContent);

        String notWritableText = toOutput + "/notWritableText.txt";
        Files.createFile(Path.of(notWritableText));
        new File(notWritableText).setWritable(false);
        ls = new Ls(true, true, true,
                toOutput + "/notWritableText.txt",
                toPrint + "/test1.txt");
        assertThrows(IOException.class, ls::returnOrOutputContent);
        new File(notWritableText).setWritable(true);
        Files.deleteIfExists(Path.of(notWritableText));

        String unavailableDir = toOutput + "/unavailableDir";
        ls = new Ls(true, true, true,
                unavailableDir + "/test.txt",
                toPrint + "/test1.txt");
        assertThrows(IOException.class, ls::returnOrOutputContent);
        Files.deleteIfExists(Path.of(unavailableDir));


    }


}
