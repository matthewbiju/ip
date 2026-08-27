package rex.command;

import rex.TaskList;
import rex.Ui;

/** Shows every task in the list. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTaskList(tasks);
    }
}
