package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserInputParserTest {

    private final static String PATH_TO_BED ="src/test/resources/genome/compare/correct.bed";

    private final static String PATH_TO_BAM_1 = "src/test/resources/genome/compare/testDad4.bam";

    private final static String PATH_TO_BAM_2 = "src/test/resources/genome/compare/testMother4.bam";

    private final static String PATH_TO_BAM_3 = "src/test/resources/genome/compare/testSon4.bam";

    private final static String CHECK_STR_1 ="Similarity percentage for each chromosome:\n" +
            "\tName of chromosome: 4. Similarity percentage: 100.0%\n" +
            "\tNumber of nucleotides compared: 884\n" +
            "\tSimilarity percentage for each gene from this chromosome:\n" +
            "\t\tName of gene: UPNWPYFPJQ. Similarity percentage: 100.0%\n" +
            "Count of chromosomes with 99.7+% similarity: 1\n" +
            "Count of dissimilar chromosomes: 0\n" +
            "These persons are child and parent.";
    private final static String CHECK_STR_2 ="Comparison of father and son genomes:\n" +
            "Similarity percentage for each chromosome:\n" +
            "\tName of chromosome: 4. Similarity percentage: 100.0%\n" +
            "\tNumber of nucleotides compared: 884\n" +
            "\tSimilarity percentage for each gene from this chromosome:\n" +
            "\t\tName of gene: UPNWPYFPJQ. Similarity percentage: 100.0%\n" +
            "Count of chromosomes with 99.7+% similarity: 1\n" +
            "Count of dissimilar chromosomes: 0\n" +
            "These persons are child and parent.\n" +
            "Comparison of mother and son genomes:\n" +
            "Similarity percentage for each chromosome:\n" +
            "\tName of chromosome: 4. Similarity percentage: 98.29488465396189%\n" +
            "\tNumber of nucleotides compared: 997\n" +
            "\tSimilarity percentage for each gene from this chromosome:\n" +
            "\t\tName of gene: UPNWPYFPJQ. Similarity percentage: 98.29488465396189%\n" +
            "Count of chromosomes with 99.7+% similarity: 0\n" +
            "Count of dissimilar chromosomes: 1\n" +
            "These persons are not child and parent.";
    @Test
    public void CorrectUserInputTestHelpMessageLong() throws Exception {
        String[] args = {"--help"};
        assertEquals("help", UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestHelpMessageShort() throws Exception {
        String[] args = {"-h"};
        assertEquals("help", UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestCmp2Long() throws Exception {
        String[] args = {"-compare2", PATH_TO_BAM_1, PATH_TO_BAM_3, PATH_TO_BED};
        assertEquals(CHECK_STR_1, UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestCmp2Short() throws Exception {
        String[] args = {"-c2", PATH_TO_BAM_1, PATH_TO_BAM_3, PATH_TO_BED};
        assertEquals(CHECK_STR_1, UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestCmp3Long() throws Exception {
        String[] args = {"--compare3", PATH_TO_BAM_1, PATH_TO_BAM_2, PATH_TO_BAM_3, PATH_TO_BED};
        assertEquals(CHECK_STR_2, UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestCmp3Short() throws Exception {
        String[] args = {"-c3", PATH_TO_BAM_1, PATH_TO_BAM_2, PATH_TO_BAM_3, PATH_TO_BED};
        assertEquals(CHECK_STR_2, UserInputParser.parseInput(args));
    }
}
