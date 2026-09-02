package rex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rex.task.Deadline;
import rex.task.Event;

/**
 * Tests reading the arguments of the commands that take more than a word.
 *
 * A parsed task is checked through its save format rather than field by field,
 * since that one string carries the description, the done flag and every date
 * the task holds, and comparing it catches a value landing in the wrong place
 * as readily as a wrong value.
 *
 * Test methods are deliberately not public, for the reason given in
 * TaskDateTimeTest.
 */
public class ParserTest {
    @Test
    void parseDeadline_descriptionAndDate_returnsDeadline() throws RexException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");

        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void parseDeadline_dateCarryingATime_timeKept() throws RexException {
        Deadline deadline = Parser.parseDeadline("submit report /by 2019-10-15 1800");

        assertEquals("D | 0 | submit report | 2019-10-15 1800", deadline.toSaveFormat());
    }

    @Test
    void parseDeadline_surroundingWhitespace_trimmed() throws RexException {
        Deadline deadline = Parser.parseDeadline("  return book  /by 2019-10-15 ");

        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void parseDeadline_missingBy_askedForABy() {
        RexException thrown = assertThrows(RexException.class,
                () -> Parser.parseDeadline("return book"));

        assertTrue(thrown.getMessage().contains("/by"), thrown.getMessage());
    }

    @Test
    void parseDeadline_byWithNothingAfterIt_askedForABy() {
        // The message matters as much as the refusal here. A "/by" with nothing
        // after it splits into an empty date, which would otherwise be reported
        // as an unreadable date rather than as a missing one.
        RexException thrown = assertThrows(RexException.class,
                () -> Parser.parseDeadline("return book /by "));

        assertTrue(thrown.getMessage().contains("/by"), thrown.getMessage());
    }

    @Test
    void parseDeadline_emptyDescription_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseDeadline("  /by 2019-10-15"));
    }

    @Test
    void parseDeadline_unreadableDate_exceptionThrown() {
        // A date the user got wrong has to arrive as a RexException. Left as the
        // IllegalArgumentException that TaskDateTime throws, it would end the
        // session instead of being answered with a message.
        assertThrows(RexException.class, () -> Parser.parseDeadline("return book /by tomorrow"));
    }

    @Test
    void parseEvent_descriptionAndBothTimes_returnsEvent() throws RexException {
        Event event = Parser.parseEvent(
                "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");

        assertEquals("E | 0 | project meeting | 2019-10-15 1400 | 2019-10-15 1600",
                event.toSaveFormat());
    }

    @Test
    void parseEvent_datesWithoutTimes_returnsEvent() throws RexException {
        Event event = Parser.parseEvent("company retreat /from 2019-10-18 /to 2019-10-20");

        assertEquals("E | 0 | company retreat | 2019-10-18 | 2019-10-20", event.toSaveFormat());
    }

    @Test
    void parseEvent_missingFrom_exceptionThrown() {
        assertThrows(RexException.class,
                () -> Parser.parseEvent("project meeting /to 2019-10-15 1600"));
    }

    @Test
    void parseEvent_missingTo_exceptionThrown() {
        assertThrows(RexException.class,
                () -> Parser.parseEvent("project meeting /from 2019-10-15 1400"));
    }

    @Test
    void parseEvent_emptyDescription_exceptionThrown() {
        assertThrows(RexException.class,
                () -> Parser.parseEvent(" /from 2019-10-15 1400 /to 2019-10-15 1600"));
    }

    @Test
    void parseEvent_unreadableStartDate_exceptionThrown() {
        assertThrows(RexException.class,
                () -> Parser.parseEvent("meeting /from someday /to 2019-10-15 1600"));
    }

    @Test
    void parseTaskNumber_plainNumber_returnsNumber() throws RexException {
        assertEquals(3, Parser.parseTaskNumber("3"));
    }

    @Test
    void parseTaskNumber_surroundingWhitespace_ignored() throws RexException {
        assertEquals(3, Parser.parseTaskNumber("  3  "));
    }

    @Test
    void parseTaskNumber_outsideAnyList_stillReturned() {
        // Whether a number names a task depends on how many tasks there are,
        // which the task list knows and the parser does not, so a number that
        // no task could ever carry is still read rather than refused here.
        assertEquals(0, assertDoesNotThrowNumber("0"));
        assertEquals(-1, assertDoesNotThrowNumber("-1"));
    }

    @Test
    void parseTaskNumber_word_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseTaskNumber("three"));
    }

    @Test
    void parseTaskNumber_decimal_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseTaskNumber("1.5"));
    }

    @Test
    void parseTaskNumber_nothingGiven_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseTaskNumber(""));
    }

    @Test
    void parseKeyword_singleWord_returnsKeyword() throws RexException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    void parseKeyword_severalWords_keptAsOnePhrase() throws RexException {
        // Splitting here would turn a search for one phrase into a search for
        // any of its words, which finds far more than was asked for.
        assertEquals("return book", Parser.parseKeyword("  return book  "));
    }

    @Test
    void parseKeyword_nothingGiven_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseKeyword("   "));
    }

    /** Reads a task number, turning a refusal into a test failure. */
    private static int assertDoesNotThrowNumber(String argument) {
        try {
            return Parser.parseTaskNumber(argument);
        } catch (RexException e) {
            throw new AssertionError("Expected " + argument + " to be read as a number", e);
        }
    }
}
