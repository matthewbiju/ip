import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Rex {
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        // Loaded between the greeting and the invitation to type, so that
        // anything the load has to report appears before the user is asked
        // for a command.
        Storage storage = new Storage("data", "rex.txt");
        ArrayList<Task> tasks = loadTasks(storage, ui);

        ui.showReady();

        String input = ui.readCommand();
        CommandType command = parseCommandType(commandWordOf(input));
        while (command != CommandType.BYE) {
            String argument = argumentOf(input);
            try {
                switch (command) {
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case MARK: {
                    int index = parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsDone();
                    ui.showMarked(tasks.get(index));
                    break;
                }
                case UNMARK: {
                    int index = parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsNotDone();
                    ui.showUnmarked(tasks.get(index));
                    break;
                }
                case DELETE: {
                    int index = parseTaskIndex(argument, tasks.size());
                    Task removed = tasks.remove(index);
                    ui.showRemoved(removed, tasks.size());
                    break;
                }
                case TODO: {
                    String description = argument.trim();
                    if (description.isEmpty()) {
                        throw new RexException("Ruff! The description of a todo cannot be empty.");
                    }
                    tasks.add(new ToDo(description));
                    ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
                    break;
                }
                case DEADLINE: {
                    String[] parts = argument.split(" /by ", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new RexException("Ruff! The description of a deadline cannot be empty.");
                    }
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new RexException("Ruff! A deadline needs a '/by' date, "
                                + "e.g. deadline return book /by 2019-10-15.");
                    }
                    tasks.add(new Deadline(description, parseDateTime(parts[1])));
                    ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
                    break;
                }
                case EVENT: {
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
                    tasks.add(new Event(description, parseDateTime(toParts[0]),
                            parseDateTime(toParts[1])));
                    ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
                    break;
                }
                case ON: {
                    LocalDate day = parseQueryDate(argument);
                    ui.showTasksOn(TaskDateTime.formatDate(day), tasks, day);
                    break;
                }
                case UNKNOWN:
                default:
                    throw new RexException("Woof? I don't know what that means :-(");
                }
                // Reached only if the command succeeded, so the list is saved
                // exactly when it actually changed.
                if (command.isTaskListChanged()) {
                    saveTasks(storage, tasks, ui);
                }
            } catch (RexException e) {
                ui.showError(e.getMessage());
            }
            input = ui.readCommand();
            command = parseCommandType(commandWordOf(input));
        }
        ui.showFarewell();
        ui.close();
    }

    /**
     * Loads the saved tasks, starting with an empty list if they could not be
     * read. Refusing to start would leave the user with no way to use the
     * program at all, which is worse than starting fresh.
     */
    private static ArrayList<Task> loadTasks(Storage storage, Ui ui) {
        try {
            ArrayList<Task> tasks = storage.load();
            int skipped = storage.getSkippedLineCount();
            if (skipped > 0) {
                ui.showSkippedLines(skipped, storage.getFile());
            }
            return tasks;
        } catch (IOException e) {
            ui.showLoadingError(storage.getFile());
            return new ArrayList<>();
        }
    }

    /**
     * Saves the tasks, warning the user if the save failed but letting the
     * session carry on. A failed save is not the user's mistake, so it is not
     * reported as a RexException (which would print "OOPS!!!" and suggest they
     * typed something wrong), and it must not end the session: the tasks they
     * have added are still usable in memory.
     */
    private static void saveTasks(Storage storage, ArrayList<Task> tasks, Ui ui) {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showSavingError(storage.getFile());
        }
    }

    /** Returns the first space-separated word of the input, e.g. "todo" from "todo borrow book". */
    private static String commandWordOf(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? input : input.substring(0, spaceIndex);
    }

    /** Returns everything after the first word, or "" if there is no argument. */
    private static String argumentOf(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? "" : input.substring(spaceIndex + 1);
    }

    /** Maps a command word to its CommandType, or UNKNOWN if it isn't recognized. */
    private static CommandType parseCommandType(String commandWord) {
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Parses a date the user typed after /by, /from or /to.
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

    /**
     * Parses the day the user asked about with the on command.
     *
     * Unlike a task's own date this is always a whole day, so a time of day is
     * rejected rather than quietly ignored: "what's on the 15th at 6pm" is not
     * a question this command answers, and silently dropping the time would
     * hide that.
     */
    private static LocalDate parseQueryDate(String argument) throws RexException {
        try {
            return LocalDate.parse(argument.trim());
        } catch (DateTimeParseException e) {
            throw new RexException("Woof! Tell me which day to look at, written as yyyy-mm-dd, "
                    + "e.g. on 2019-10-15.");
        }
    }

    /**
     * Parses a mark/unmark argument into a 0-based task index, throwing a
     * RexException (rather than letting NumberFormatException or an
     * out-of-range index propagate) if it isn't a valid task number.
     */
    private static int parseTaskIndex(String argument, int taskCount) throws RexException {
        int index;
        try {
            index = Integer.parseInt(argument.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new RexException("Woof! \"" + argument.trim() + "\" isn't a valid task number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new RexException("Woof! There's no task numbered " + argument.trim()
                    + " in your bowl.");
        }
        return index;
    }
}
