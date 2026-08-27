import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of what the user typed.
 *
 * This is the only class that knows the shape of a command: that the first
 * word names it, that a deadline's date follows "/by", that an event's times
 * are separated by "/from" and "/to". Everything it hands back is already
 * checked, so the rest of the program works with tasks and numbers rather
 * than with raw text.
 *
 * Anything the user got wrong is reported as a RexException carrying a
 * sentence meant to be shown to them as-is.
 */
public class Parser {
    /**
     * Turns a line the user typed into the command it asks for.
     *
     * Note that the command is only built here, not carried out: nothing in
     * this class looks at the task list, so a command that names a task by
     * number keeps the number and checks it when it runs.
     *
     * @param input the whole line the user typed.
     * @return the command it asks for, ready to be run.
     * @throws RexException if the line names a command but describes it
     *     wrongly, e.g. a deadline with no date.
     */
    public static Command parse(String input) throws RexException {
        String argument = parseArgument(input);
        switch (parseCommandType(input)) {
        case LIST:
            return new ListCommand();
        case MARK:
            return new MarkCommand(parseTaskNumber(argument));
        case UNMARK:
            return new UnmarkCommand(parseTaskNumber(argument));
        case DELETE:
            return new DeleteCommand(parseTaskNumber(argument));
        case TODO:
            return new AddCommand(parseTodo(argument));
        case DEADLINE:
            return new AddCommand(parseDeadline(argument));
        case EVENT:
            return new AddCommand(parseEvent(argument));
        case ON:
            return new OnCommand(parseQueryDate(argument));
        case BYE:
            return new ExitCommand();
        case UNKNOWN:
        default:
            return new UnknownCommand();
        }
    }

    /**
     * Returns which command the input names, or UNKNOWN if it names none.
     *
     * @param input the whole line the user typed.
     */
    public static CommandType parseCommandType(String input) {
        try {
            return CommandType.valueOf(commandWordOf(input).toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Returns everything after the command word, or "" if nothing follows it.
     *
     * @param input the whole line the user typed.
     */
    public static String parseArgument(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? "" : input.substring(spaceIndex + 1);
    }

    /**
     * Reads the argument of a todo command.
     *
     * @param argument everything after the word "todo".
     * @return the task it describes.
     * @throws RexException if no description was given.
     */
    public static ToDo parseTodo(String argument) throws RexException {
        String description = argument.trim();
        if (description.isEmpty()) {
            throw new RexException("Ruff! The description of a todo cannot be empty.");
        }
        return new ToDo(description);
    }

    /**
     * Reads the argument of a deadline command, e.g.
     * "return book /by 2019-10-15".
     *
     * @param argument everything after the word "deadline".
     * @return the task it describes.
     * @throws RexException if the description or the /by date is missing, or
     *     the date cannot be read.
     */
    public static Deadline parseDeadline(String argument) throws RexException {
        String[] parts = argument.split(" /by ", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new RexException("Ruff! The description of a deadline cannot be empty.");
        }
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new RexException("Ruff! A deadline needs a '/by' date, "
                    + "e.g. deadline return book /by 2019-10-15.");
        }

        return new Deadline(description, parseDateTime(parts[1]));
    }

    /**
     * Reads the argument of an event command, e.g.
     * "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600".
     *
     * @param argument everything after the word "event".
     * @return the task it describes.
     * @throws RexException if the description, the /from time or the /to time
     *     is missing, or either date cannot be read.
     */
    public static Event parseEvent(String argument) throws RexException {
        String[] fromParts = argument.split(" /from ", 2);
        String description = fromParts[0].trim();
        if (description.isEmpty()) {
            throw new RexException("Ruff! The description of an event cannot be empty.");
        }
        if (fromParts.length < 2 || fromParts[1].trim().isEmpty()) {
            throw new RexException("Ruff! An event needs a '/from' time, e.g. event "
                    + "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600.");
        }

        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[1].trim().isEmpty()) {
            throw new RexException("Ruff! An event needs a '/to' time, e.g. event "
                    + "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600.");
        }

        return new Event(description, parseDateTime(toParts[0]), parseDateTime(toParts[1]));
    }

    /**
     * Reads a task number as the user typed it, counting from 1.
     *
     * Only whether it is a number at all is checked here. Whether it names a
     * task depends on how many tasks there are, which the task list knows and
     * this class does not.
     *
     * @param argument the number the user typed, e.g. "3".
     * @throws RexException if the argument is not a number.
     */
    public static int parseTaskNumber(String argument) throws RexException {
        try {
            return Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            throw new RexException("Woof! \"" + argument.trim() + "\" isn't a valid task number.");
        }
    }

    /**
     * Reads the day the user asked about with the on command.
     *
     * Unlike a task's own date this is always a whole day, so a time of day is
     * refused rather than quietly ignored: "what's on the 15th at 6pm" is not
     * a question this command answers, and dropping the time would hide that.
     *
     * @param argument everything after the word "on".
     * @throws RexException if it is not a plain date.
     */
    public static LocalDate parseQueryDate(String argument) throws RexException {
        try {
            return LocalDate.parse(argument.trim());
        } catch (DateTimeParseException e) {
            throw new RexException("Woof! Tell me which day to look at, written as yyyy-mm-dd, "
                    + "e.g. on 2019-10-15.");
        }
    }

    /** Returns the first space-separated word, e.g. "todo" from "todo borrow book". */
    private static String commandWordOf(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? input : input.substring(0, spaceIndex);
    }

    /**
     * Reads a date the user typed after /by, /from or /to.
     *
     * TaskDateTime reports a date it cannot read as an IllegalArgumentException,
     * which would end the program. It is turned into a RexException here so that
     * it is shown as an "OOPS!!!" message and the session carries on, the same
     * as any other mistake in a command.
     */
    private static TaskDateTime parseDateTime(String argument) throws RexException {
        try {
            return TaskDateTime.parse(argument);
        } catch (IllegalArgumentException e) {
            throw new RexException("Woof! I don't understand the date \"" + argument.trim()
                    + "\". Write it as yyyy-mm-dd, e.g. 2019-10-15, "
                    + "optionally with a 24-hour time, e.g. 2019-10-15 1800.");
        }
    }
}
