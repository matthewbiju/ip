/** Removes a task from the list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that removes a task.
     *
     * @param taskNumber the number the user gave, counting from 1. Whether a
     *     task has it is checked when the command runs, since only then is
     *     there a list to ask.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws RexException {
        Task removed = tasks.deleteByNumber(taskNumber);
        ui.showRemoved(removed, tasks.size());
    }

    @Override
    public boolean isTaskListChanged() {
        return true;
    }
}
