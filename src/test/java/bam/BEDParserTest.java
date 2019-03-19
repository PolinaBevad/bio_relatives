package bam;

import exception.InvalidBEDFileException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

/**
 * Tests the {@link BEDParser} class methods.
 *
 * @author Sergey Hvatov
 */
public class BEDParserTest
{
    /**
     * Testing {@link BEDParser} object.
     */
    @Mock
    private BEDParser parser;

    /**
     * Create new mockito rule for creating mockito mock objects.
     */
    @Rule public MockitoRule rule = MockitoJUnit.rule();

    private static final String pathToNonExistentFile = "wrong/path/file.bed";
    private static final String pathToNonExistentFileWithWrongExt = "wrong/path/file.txt";
    private static final String pathToIncorrectFile = "/bio_relatives/src/test/resources/incorrect.bed";
    private static final String pathToCorrectFile = "/bio_relatives/src/test/resources/correct.bed";
    private static final String pathToWrongExtFile = "/bio_relatives/src/test/resources/wrong.txt";
    private static final String pathToFolder = "/bio_relatives/src/test/resources/";

    //TODO Add testing of BEDParser here
}
