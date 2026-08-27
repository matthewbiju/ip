/** Marks a task as not done again. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks a task as not done.
     *
     * @param taskNumber the number the user gave, counting from 1.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws RexException {
        Task task = tasks.getByNumber(taskNumber);
        task.markAsNotDone();
        ui.showUnmarked(task);
    }

    @Override
    public boolean isTaskListChanged() {
        return true;
    }
}
