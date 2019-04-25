package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserInputParserTest {

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
        String[] args = "--compare2 value1 value2".split(" ");
        assertEquals("cmp2", UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestCmp2Short() throws Exception {
        String[] args = {"-c2", "value1", "value2"};
        assertEquals("cmp2", UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestCmp3Long() throws Exception {
        String[] args = {"--compare3", "value1", "value2", "value3"};
        assertEquals("cmp3", UserInputParser.parseInput(args));
    }

    @Test
    public void CorrectUserInputTestCmp3Short() throws Exception {
        String[] args = {"-c3", "value1", "value2", "value3"};
        assertEquals("cmp3", UserInputParser.parseInput(args));
    }
}
