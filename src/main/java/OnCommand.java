import java.time.LocalDate;

/** Shows the tasks falling on one particular day. */
public class OnCommand extends Command {
    private final LocalDate day;

    /**
     * Creates a command that looks at one day.
     *
     * @param day the day the user asked about.
     */
    public OnCommand(LocalDate day) {
        this.day = day;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTasksOn(TaskDateTime.formatDate(day), tasks, tasks.findIndicesOn(day));
    }
}
