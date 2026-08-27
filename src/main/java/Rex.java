import java.io.IOException;
import java.time.LocalDate;

public class Rex {
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        // Loaded between the greeting and the invitation to type, so that
        // anything the load has to report appears before the user is asked
        // for a command.
        Storage storage = new Storage("data", "rex.txt");
        TaskList tasks = loadTasks(storage, ui);

        ui.showReady();

        String input = ui.readCommand();
        CommandType command = Parser.parseCommandType(input);
        while (command != CommandType.BYE) {
            String argument = Parser.parseArgument(input);
            try {
                switch (command) {
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case MARK: {
                    int index = Parser.parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsDone();
                    ui.showMarked(tasks.get(index));
                    break;
                }
                case UNMARK: {
                    int index = Parser.parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsNotDone();
                    ui.showUnmarked(tasks.get(index));
                    break;
                }
                case DELETE: {
                    int index = Parser.parseTaskIndex(argument, tasks.size());
                    Task removed = tasks.delete(index);
                    ui.showRemoved(removed, tasks.size());
                    break;
                }
                case TODO: {
                    Task task = Parser.parseTodo(argument);
                    tasks.add(task);
                    ui.showAdded(task, tasks.size());
                    break;
                }
                case DEADLINE: {
                    Task task = Parser.parseDeadline(argument);
                    tasks.add(task);
                    ui.showAdded(task, tasks.size());
                    break;
                }
                case EVENT: {
                    Task task = Parser.parseEvent(argument);
                    tasks.add(task);
                    ui.showAdded(task, tasks.size());
                    break;
                }
                case ON: {
                    LocalDate day = Parser.parseQueryDate(argument);
                    ui.showTasksOn(TaskDateTime.formatDate(day), tasks, tasks.findIndicesOn(day));
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
            command = Parser.parseCommandType(input);
        }
        ui.showFarewell();
        ui.close();
    }

    /**
     * Loads the saved tasks, starting with an empty list if they could not be
     * read. Refusing to start would leave the user with no way to use the
     * program at all, which is worse than starting fresh.
     */
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            TaskList tasks = new TaskList(storage.load());
            int skipped = storage.getSkippedLineCount();
            if (skipped > 0) {
                ui.showSkippedLines(skipped, storage.getFile());
            }
            return tasks;
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
    private static void saveTasks(Storage storage, TaskList tasks, Ui ui) {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            ui.showSavingError(storage.getFile());
        }
    }
}
