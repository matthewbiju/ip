package rex;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import rex.task.Task;

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

    /**
     * Where output is collected while the GUI is being served, or null when
     * output goes straight to the screen as it does for the console.
     */
    private StringBuilder captured;

    /** Creates a Ui that reads the user's commands from the keyboard. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts collecting output instead of printing it.
     *
     * The GUI shows a whole reply at once in a dialog box rather than a line
     * at a time, so it collects what the console would have printed and takes
     * it back as one string. Every show method below is unaffected: they all
     * call show(), which decides where the line goes.
     */
    public void startCapture() {
        captured = new StringBuilder();
    }

    /**
     * Returns everything collected since startCapture(), and goes back to
     * printing.
     *
     * @return the collected output, without a trailing newline.
     */
    public String takeCapture() {
        String collected = captured.toString().stripTrailing();
        captured = null;
        return collected;
    }

    /**
     * Shows the banner and the opening greeting.
     *
     * Note that this stops short of inviting a command: anything the startup
     * has to report, such as a save file that could not be read, belongs
     * between the greeting and that invitation, so showReady() is separate.
     */
    public void showWelcome() {
        show(BANNER);
        show("Woof woof! I'm Rex, your task-fetching sidekick!");
    }

    /** Invites the user to type their first command. */
    public void showReady() {
        show("What can I fetch for you today?");
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
        show("Bye! *wags tail* Hope to fetch for you again soon!");
    }

    /**
     * Reports a mistake in a command.
     *
     * @param message a complete sentence explaining what went wrong, taken
     *     from the RexException that was thrown.
     */
    public void showError(String message) {
        show("OOPS!!! " + message);
    }

    /** Confirms that a task was added, and says how many there are now. */
    public void showAdded(Task task, int taskCount) {
        show("Got it! I've fetched this task for you:");
        show("  " + formatTask(task));
        show("You now have " + taskCount + " tasks in your bowl!");
    }

    /** Confirms that a task is now done. */
    public void showMarked(Task task) {
        show("Nice catch! I've marked this task as done:");
        show("  " + formatTask(task));
    }

    /** Confirms that a task is no longer done. */
    public void showUnmarked(Task task) {
        show("Okay, putting this one back in the yard — not done yet:");
        show("  " + formatTask(task));
    }

    /** Confirms that a task was removed, and says how many are left. */
    public void showRemoved(Task task, int remainingCount) {
        show("Gotcha! I've removed this task from your bowl:");
        show("  " + formatTask(task));
        show("You now have " + remainingCount + " tasks in your bowl!");
    }

    /** Shows every task, numbered from 1. */
    public void showTaskList(TaskList tasks) {
        show("Here's what's in your bowl:");
        for (int i = 0; i < tasks.size(); i++) {
            show(numberedTask(i + 1, tasks.get(i)));
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
            show("Nothing on " + dayShown + " — your bowl's empty that day!");
            return;
        }

        show("Here's what's on " + dayShown + ":");
        showNumbered(tasks, matchingIndices);
    }

    /**
     * Shows the tasks whose description matched a search, or says that none
     * did.
     *
     * As in showTasksOn, each task keeps its number from the full list, so a
     * number seen here still names the same task for mark, unmark and delete.
     *
     * @param keyword what was searched for, shown back so that the user can
     *     see what the answer belongs to.
     * @param tasks every task the user has.
     * @param matchingIndices the positions, counting from 0, of the ones to show.
     */
    public void showMatchingTasks(String keyword, TaskList tasks, List<Integer> matchingIndices) {
        if (matchingIndices.isEmpty()) {
            show("No sign of \"" + keyword + "\" in your bowl!");
            return;
        }

        show("Here's what matches \"" + keyword + "\":");
        showNumbered(tasks, matchingIndices);
    }

    /** Prints the tasks at the given positions, each with its number in the full list. */
    private void showNumbered(TaskList tasks, List<Integer> indices) {
        for (int index : indices) {
            show(numberedTask(index + 1, tasks.get(index)));
        }
    }

    /** Warns that the save file could not be read at all, so the list starts empty. */
    public void showLoadingError(Path file) {
        show("Ruff! I couldn't read " + file + " — starting with an empty list.");
    }

    /**
     * Warns that some lines of the save file could not be understood. Reported
     * once with a count, so that a badly damaged file does not bury the
     * greeting under one message per line.
     */
    public void showSkippedLines(int skippedCount, Path file) {
        show("Ruff! I couldn't read " + skippedCount + " line(s) in "
                + file + ", so I've skipped them.");
    }

    /** Warns that the tasks could not be written to disk, without ending the session. */
    public void showSavingError(Path file) {
        show("Ruff! I couldn't save to " + file
                + " — your tasks are safe for now, but they may not survive a restart.");
    }

    /**
     * Writes one line, either to the screen or to the collected output.
     *
     * Every message in this class goes through here, so that switching between
     * the console and the GUI is a matter of where this one method sends the
     * line rather than a change to each message.
     */
    private void show(String line) {
        if (captured == null) {
            System.out.println(line);
            return;
        }
        captured.append(line).append(System.lineSeparator());
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
