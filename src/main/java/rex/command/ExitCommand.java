package rex.command;

import rex.TaskList;
import rex.Ui;

/** Says goodbye and ends the session. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showFarewell();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
