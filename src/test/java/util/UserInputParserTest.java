package util;

import cmd.CmdParser;
import cmd.Configuration;
import cmd.Operation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// TODO: make new tests
public class UserInputParserTest {

    private final static String PATH_TO_BED ="src/test/resources/genome/compare/correct.bed";

    private final static String PATH_TO_BAM_1 = "src/test/resources/genome/compare/testDad4.bam";

    private final static String PATH_TO_BAM_2 = "src/test/resources/genome/compare/testMother4.bam";

    private final static String PATH_TO_BAM_3 = "src/test/resources/genome/compare/testSon4.bam";

    private final static String CHECK_STR_0 = "## Usage\n" +
        "\tjava -jar bio_relatives.jar [-h | --help] [-io | --intermediateOutput] [-m | --mode <L | XY>] [-th | --threadsNumber <number>] [-c2 | --compare2 <first> <second> <bed>] [-c3 | --compare3 <father> <mother> <son> <bed>] \n" +
        "### Options\n" +
        "\t\n" +
        "\t`-h`, `--help` - show help message.\n" +
        "\t\n" +
        "\t`-io`, `--intermediateOutput` - key, which enables intermediate results output.\n" +
        "\t\n" +
        "\t`m`, `--mode` - defines the type of the comparator that will be used.\n" +
        "\t\n" +
        "\t`-th`, `--threadsNumber` - defines number of threads that will be used to process the genome.\n" +
        "\t\n" +
        "\t`-c2`, `--compare2` - compare genomes of two persons.\n" +
        "\t\n" +
        "\t`-c3`, `--compare3` - compare genomes of three persons (father/mother/son).\n" +
        "\t\n" +
        "### Examples\n" +
        "\t#### Comparison of the genomes of two persons\n" +
        "\t\t```\n" +
        "\t\tjava -jar bio_relatives.jar --compare2 ~/path/to/first.bam ~/path/to/second.bam ~/path/to/file.bed\n" +
        "\t\t```\n" +
        "\t#### Comparison of the genomes of three persons\n" +
        "\t\t```\n" +
        "\t\tjava -jar bio_relatives.jar --compare3 ~/path/to/father.bam ~/path/to/mother.bam ~/path/to/son.bam ~/path/to/file.bed\n" +
        "\t\t```\n" +
        "\t#### Comparison of the genomes with intermediate output\n" +
        "\t\t```\n" +
        "\t\tjava -jar bio_relatives.jar -io -c2 ~/path/to/first.bam ~/path/to/second.bam ~/path/to/file.bed\n" +
        "\t\t```\n";
    private final static String CHECK_STR_1 ="Similarity percentage for each chromosome:\n" +
            "\tName of chromosome: 4. Similarity percentage: 100.0%\n" +
            "\tNumber of nucleotides compared: 1768\n" +
            "\tSimilarity percentage for each gene from this chromosome:\n" +
            "\t\tName of gene: UPNWPYFPJQ. Similarity percentage: 100.0%\n" +
            "\t\tName of gene: UPNWfYFPJQ. Similarity percentage: 100.0%\n" +
            "Count of chromosomes with 99.7+% similarity: 1\n" +
            "Count of dissimilar chromosomes: 0\n";
    private final static String CHECK_STR_2 ="Comparison of father and son genomes:\n" +
            "Similarity percentage for each chromosome:\n" +
            "\tName of chromosome: 4. Similarity percentage: 100.0%\n" +
            "\tNumber of nucleotides compared: 1768\n" +
            "\tSimilarity percentage for each gene from this chromosome:\n" +
            "\t\tName of gene: UPNWPYFPJQ. Similarity percentage: 100.0%\n" +
            "\t\tName of gene: UPNWfYFPJQ. Similarity percentage: 100.0%\n" +
            "Count of chromosomes with 99.7+% similarity: 1\n" +
            "Count of dissimilar chromosomes: 0\n" +
            "\n" +
            "Comparison of mother and son genomes:\n" +
            "Similarity percentage for each chromosome:\n" +
            "\tName of chromosome: 4. Similarity percentage: 98.29488465396189%\n" +
            "\tNumber of nucleotides compared: 1994\n" +
            "\tSimilarity percentage for each gene from this chromosome:\n" +
            "\t\tName of gene: UPNWPYFPJQ. Similarity percentage: 98.29488465396189%\n" +
            "\t\tName of gene: UPNWfYFPJQ. Similarity percentage: 98.29488465396189%\n" +
            "Count of chromosomes with 99.7+% similarity: 0\n" +
            "Count of dissimilar chromosomes: 1\n" +
            "\n" +
            "Chromosomes from father: [4]\n" +
            "Chromosomes from mother: []\n" +
            "Number of chromosomes from father: 1\n" +
            "Number of chromosomes from mother: 0\n";

    @Test
    public void CorrectUserInputTestHelpMessageLong() throws Exception {
        String[] args = {"--help"};
        Configuration config = new CmdParser().parseCommandLine(args);
        assertEquals(CHECK_STR_0, new Operation().start(config));
    }

    @Test
    public void CorrectUserInputTestHelpMessageShort() throws Exception {
        String[] args = {"-h"};
        Configuration config = new CmdParser().parseCommandLine(args);
        assertEquals(CHECK_STR_0, new Operation().start(config));
    }

    @Test
    public void CorrectUserInputTestCmp2Long() throws Exception {
        String[] args = {"-compare2", PATH_TO_BAM_1, PATH_TO_BAM_3, PATH_TO_BED};
        Configuration config = new CmdParser().parseCommandLine(args);
        assertEquals(CHECK_STR_1, new Operation().start(config));
    }

    @Test
    public void CorrectUserInputTestCmp2Short() throws Exception {
        String[] args = {"-c2", PATH_TO_BAM_1, PATH_TO_BAM_3, PATH_TO_BED};
        Configuration config = new CmdParser().parseCommandLine(args);
        assertEquals(CHECK_STR_1, new Operation().start(config));
    }

    @Test
    public void CorrectUserInputTestCmp3Long() throws Exception {
        String[] args = {"--compare3", PATH_TO_BAM_1, PATH_TO_BAM_2, PATH_TO_BAM_3, PATH_TO_BED};
        Configuration config = new CmdParser().parseCommandLine(args);
        assertEquals(CHECK_STR_2, new Operation().start(config));
    }

    @Test
    public void CorrectUserInputTestCmp3Short() throws Exception {
        String[] args = {"-c3", PATH_TO_BAM_1, PATH_TO_BAM_2, PATH_TO_BAM_3, PATH_TO_BED};
        Configuration config = new CmdParser().parseCommandLine(args);
        assertEquals(CHECK_STR_2, new Operation().start(config));
    }
}
