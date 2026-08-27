import java.io.IOException;
import java.time.LocalDate;

/**
 * A task-tracking chatbot that reads commands from the console.
 *
 * This class holds the three parts the program is built from — the user
 * interface, the saved file, and the task list — and does little itself
 * beyond running the loop that reads a command, carries it out, and saves
 * the result.
 */
public class Rex {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * Note that the file is not read here. Loading is left to run(), because
     * anything the load has to report is shown to the user, and that has to
     * happen after the greeting rather than before it.
     *
     * @param first the first part of the path to the save file, e.g. "data".
     * @param more the remaining parts, e.g. "rex.txt".
     */
    public Rex(String first, String... more) {
        this.ui = new Ui();
        this.storage = new Storage(first, more);
    }

    public static void main(String[] args) {
        new Rex("data", "rex.txt").run();
    }

    /** Greets the user, then reads and carries out commands until told to stop. */
    public void run() {
        ui.showWelcome();
        tasks = loadTasks();
        ui.showReady();

        String input = ui.readCommand();
        CommandType command = Parser.parseCommandType(input);
        while (command != CommandType.BYE) {
            try {
                execute(command, Parser.parseArgument(input));

                // Reached only if the command succeeded, so the list is saved
                // exactly when it actually changed.
                if (command.isTaskListChanged()) {
                    saveTasks();
                }
            } catch (RexException e) {
                ui.showError(e.getMessage());
            }
            input = ui.readCommand();
            command = Parser.parseCommandType(input);
        }

        ui.showFarewell();
        ui.close();
    }

    /**
     * Carries out one command.
     *
     * @param command which command was given.
     * @param argument everything the user typed after the command word.
     * @throws RexException if the argument is not one the command accepts.
     */
    private void execute(CommandType command, String argument) throws RexException {
        switch (command) {
        case LIST:
            ui.showTaskList(tasks);
            break;
        case MARK: {
            Task task = tasks.getByNumber(Parser.parseTaskNumber(argument));
            task.markAsDone();
            ui.showMarked(task);
            break;
        }
        case UNMARK: {
            Task task = tasks.getByNumber(Parser.parseTaskNumber(argument));
            task.markAsNotDone();
            ui.showUnmarked(task);
            break;
        }
        case DELETE: {
            Task removed = tasks.deleteByNumber(Parser.parseTaskNumber(argument));
            ui.showRemoved(removed, tasks.size());
            break;
        }
        case TODO:
            addTask(Parser.parseTodo(argument));
            break;
        case DEADLINE:
            addTask(Parser.parseDeadline(argument));
            break;
        case EVENT:
            addTask(Parser.parseEvent(argument));
            break;
        case ON: {
            LocalDate day = Parser.parseQueryDate(argument);
            ui.showTasksOn(TaskDateTime.formatDate(day), tasks, tasks.findIndicesOn(day));
            break;
        }
        case UNKNOWN:
        default:
            throw new RexException("Woof? I don't know what that means :-(");
        }
    }

    /** Adds a task and confirms it, which is the same for all three task types. */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
    }

    /**
     * Loads the saved tasks, starting with an empty list if they could not be
     * read. Refusing to start would leave the user with no way to use the
     * program at all, which is worse than starting fresh.
     */
    private TaskList loadTasks() {
        try {
            TaskList loaded = new TaskList(storage.load());
            int skipped = storage.getSkippedLineCount();
            if (skipped > 0) {
                ui.showSkippedLines(skipped, storage.getFile());
            }
            return loaded;
        } catch (IOException e) {
            ui.showLoadingError(storage.getFile());
            return new TaskList();
        }
    }

    /**
     * Saves the tasks, warning the user if the save failed but letting the
     * session carry on. A failed save is not the user's mistake, so it is not
     * reported as a RexException (which would print "OOPS!!!" and suggest they
     * typed something wrong), and it must not end the session: the tasks they
     * have added are still usable in memory.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            ui.showSavingError(storage.getFile());
        }
    }
}
