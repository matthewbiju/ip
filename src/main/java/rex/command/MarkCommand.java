package rex.command;

import rex.RexException;
import rex.TaskList;
import rex.Ui;
import rex.task.Task;

/** Marks a task as done. */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks a task as done.
     *
     * @param taskNumber the number the user gave, counting from 1.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws RexException {
        Task task = tasks.getByNumber(taskNumber);
        task.markAsDone();
        ui.showMarked(task);
    }

    @Override
    public boolean isTaskListChanged() {
        return true;
    }
}
