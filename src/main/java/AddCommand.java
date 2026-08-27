/** Adds a task to the list. Covers todos, deadlines and events alike. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task the user described, already built by the parser.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
    }

    @Override
    public boolean isTaskListChanged() {
        return true;
    }
}
