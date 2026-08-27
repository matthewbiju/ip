import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Rex {
    public static void main(String[] args) {
        String banner = " ____  _______  __\n"
                + "|  _ \\| ____\\ \\/ /\n"
                + "| |_) |  _|  \\  / \n"
                + "|  _ <| |___ /  \\ \n"
                + "|_| \\_\\_____/_/\\_\\\n";
        System.out.println(banner);
        System.out.println("Woof woof! I'm Rex, your task-fetching sidekick!");

        // Loaded between the two greeting lines so that anything the load has
        // to report appears before the user is invited to type a command.
        Storage storage = new Storage("data", "rex.txt");
        ArrayList<Task> tasks = loadTasks(storage);

        System.out.println("What can I fetch for you today?");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        CommandType command = parseCommandType(commandWordOf(input));
        while (command != CommandType.BYE) {
            String argument = argumentOf(input);
            try {
                switch (command) {
                case LIST:
                    System.out.println("Here's what's in your bowl:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + taskLine(tasks.get(i)));
                    }
                    break;
                case MARK: {
                    int index = parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsDone();
                    System.out.println("Nice catch! I've marked this task as done:");
                    System.out.println("  " + taskLine(tasks.get(index)));
                    break;
                }
                case UNMARK: {
                    int index = parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsNotDone();
                    System.out.println("Okay, putting this one back in the yard — not done yet:");
                    System.out.println("  " + taskLine(tasks.get(index)));
                    break;
                }
                case DELETE: {
                    int index = parseTaskIndex(argument, tasks.size());
                    Task removed = tasks.remove(index);
                    System.out.println("Gotcha! I've removed this task from your bowl:");
                    System.out.println("  " + taskLine(removed));
                    System.out.println("You now have " + tasks.size() + " tasks in your bowl!");
                    break;
                }
                case TODO: {
                    String description = argument.trim();
                    if (description.isEmpty()) {
                        throw new RexException("Ruff! The description of a todo cannot be empty.");
                    }
                    tasks.add(new ToDo(description));
                    printAddedConfirmation(tasks.get(tasks.size() - 1), tasks.size());
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
                    printAddedConfirmation(tasks.get(tasks.size() - 1), tasks.size());
                    break;
                }
                case EVENT: {
                    String[] fromParts = argument.split(" /from ", 2);
                    String description = fromParts[0].trim();
                    if (description.isEmpty()) {
                        throw new RexException("Ruff! The description of an event cannot be empty.");
                    }
                    if (fromParts.length < 2 || fromParts[1].trim().isEmpty()) {
                        throw new RexException("Ruff! An event needs a '/from' time, "
                                + "e.g. event project meeting /from Mon 2pm /to 4pm.");
                    }
                    String[] toParts = fromParts[1].split(" /to ", 2);
                    if (toParts.length < 2 || toParts[1].trim().isEmpty()) {
                        throw new RexException("Ruff! An event needs a '/to' time, "
                                + "e.g. event project meeting /from Mon 2pm /to 4pm.");
                    }
                    tasks.add(new Event(description, toParts[0].trim(), toParts[1].trim()));
                    printAddedConfirmation(tasks.get(tasks.size() - 1), tasks.size());
                    break;
                }
                case UNKNOWN:
                default:
                    throw new RexException("Woof? I don't know what that means :-(");
                }
                // Reached only if the command succeeded, so the list is saved
                // exactly when it actually changed.
                if (command.isTaskListChanged()) {
                    saveTasks(storage, tasks);
                }
            } catch (RexException e) {
                System.out.println("OOPS!!! " + e.getMessage());
            }
            input = scanner.nextLine();
            command = parseCommandType(commandWordOf(input));
        }
        System.out.println("Bye! *wags tail* Hope to fetch for you again soon!");
        scanner.close();
    }

    /**
     * Loads the saved tasks, starting with an empty list if they could not be
     * read. Refusing to start would leave the user with no way to use the
     * program at all, which is worse than starting fresh.
     */
    private static ArrayList<Task> loadTasks(Storage storage) {
        try {
            ArrayList<Task> tasks = storage.load();
            int skipped = storage.getSkippedLineCount();
            if (skipped > 0) {
                // Reported once with a count, so that a badly damaged file
                // does not bury the greeting under one message per line.
                System.out.println("Ruff! I couldn't read " + skipped + " line(s) in "
                        + storage.getFile() + ", so I've skipped them.");
            }
            return tasks;
        } catch (IOException e) {
            System.out.println("Ruff! I couldn't read " + storage.getFile()
                    + " — starting with an empty list.");
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
    private static void saveTasks(Storage storage, ArrayList<Task> tasks) {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            System.out.println("Ruff! I couldn't save to " + storage.getFile()
                    + " — your tasks are safe for now, but they may not survive a restart.");
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
            throw new RexException("Woof! There's no task numbered " + argument.trim() + " in your bowl.");
        }
        return index;
    }

    private static String taskLine(Task task) {
        return "[" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task.description + task.getDetails();
    }

    private static void printAddedConfirmation(Task task, int taskCount) {
        System.out.println("Got it! I've fetched this task for you:");
        System.out.println("  " + taskLine(task));
        System.out.println("You now have " + taskCount + " tasks in your bowl!");
    }
}
