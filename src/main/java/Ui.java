import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Everything the user sees and types.
 *
 * This is the only class that reads from the keyboard or writes to the screen.
 * Keeping that in one place means the wording of a message can be changed
 * without touching the logic that decided to show it, and that the rest of the
 * program never has to think about how a task is laid out on a line.
 */
public class Ui {
    private static final String BANNER = " ____  _______  __\n"
            + "|  _ \\| ____\\ \\/ /\n"
            + "| |_) |  _|  \\  / \n"
            + "|  _ <| |___ /  \\ \n"
            + "|_| \\_\\_____/_/\\_\\\n";

    private final Scanner scanner;

    /** Creates a Ui that reads the user's commands from the keyboard. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows the banner and the opening greeting.
     *
     * Note that this stops short of inviting a command: anything the startup
     * has to report, such as a save file that could not be read, belongs
     * between the greeting and that invitation, so showReady() is separate.
     */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Woof woof! I'm Rex, your task-fetching sidekick!");
    }

    /** Invites the user to type their first command. */
    public void showReady() {
        System.out.println("What can I fetch for you today?");
    }

    /** Reads one line of input from the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Stops reading input. Called once the session is over. */
    public void close() {
        scanner.close();
    }

    /** Says goodbye as the program exits. */
    public void showFarewell() {
        System.out.println("Bye! *wags tail* Hope to fetch for you again soon!");
    }

    /**
     * Reports a mistake in a command.
     *
     * @param message a complete sentence explaining what went wrong, taken
     *     from the RexException that was thrown.
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    /** Confirms that a task was added, and says how many there are now. */
    public void showAdded(Task task, int taskCount) {
        System.out.println("Got it! I've fetched this task for you:");
        System.out.println("  " + formatTask(task));
        System.out.println("You now have " + taskCount + " tasks in your bowl!");
    }

    /** Confirms that a task is now done. */
    public void showMarked(Task task) {
        System.out.println("Nice catch! I've marked this task as done:");
        System.out.println("  " + formatTask(task));
    }

    /** Confirms that a task is no longer done. */
    public void showUnmarked(Task task) {
        System.out.println("Okay, putting this one back in the yard — not done yet:");
        System.out.println("  " + formatTask(task));
    }

    /** Confirms that a task was removed, and says how many are left. */
    public void showRemoved(Task task, int remainingCount) {
        System.out.println("Gotcha! I've removed this task from your bowl:");
        System.out.println("  " + formatTask(task));
        System.out.println("You now have " + remainingCount + " tasks in your bowl!");
    }

    /** Shows every task, numbered from 1. */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here's what's in your bowl:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(numberedTask(i + 1, tasks.get(i)));
        }
    }

    /**
     * Shows the tasks at the given positions, or says that the day has none.
     *
     * Each task keeps the number it has in the full list rather than being
     * renumbered from 1, so a number seen here still refers to the same task
     * for mark, unmark and delete. That is why positions are passed in
     * alongside the list, instead of just the matching tasks.
     *
     * @param dayShown the day, already in display form, e.g. "Oct 15 2019".
     * @param tasks every task the user has.
     * @param matchingIndices the positions, counting from 0, of the ones to show.
     */
    public void showTasksOn(String dayShown, TaskList tasks, List<Integer> matchingIndices) {
        if (matchingIndices.isEmpty()) {
            System.out.println("Nothing on " + dayShown + " — your bowl's empty that day!");
            return;
        }

        System.out.println("Here's what's on " + dayShown + ":");
        for (int index : matchingIndices) {
            System.out.println(numberedTask(index + 1, tasks.get(index)));
        }
    }

    /** Warns that the save file could not be read at all, so the list starts empty. */
    public void showLoadingError(Path file) {
        System.out.println("Ruff! I couldn't read " + file + " — starting with an empty list.");
    }

    /**
     * Warns that some lines of the save file could not be understood. Reported
     * once with a count, so that a badly damaged file does not bury the
     * greeting under one message per line.
     */
    public void showSkippedLines(int skippedCount, Path file) {
        System.out.println("Ruff! I couldn't read " + skippedCount + " line(s) in "
                + file + ", so I've skipped them.");
    }

    /** Warns that the tasks could not be written to disk, without ending the session. */
    public void showSavingError(Path file) {
        System.out.println("Ruff! I couldn't save to " + file
                + " — your tasks are safe for now, but they may not survive a restart.");
    }

    /** Returns a task as it appears in a numbered list, e.g. "3.[D][ ] return book (by: Oct 15 2019)". */
    private String numberedTask(int number, Task task) {
        return number + "." + formatTask(task);
    }

    /** Returns a task's one-line form, e.g. "[D][ ] return book (by: Oct 15 2019)". */
    private String formatTask(Task task) {
        return "[" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] "
                + task.getDescription() + task.getDetails();
    }
}
