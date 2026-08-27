/**
 * The set of recognized command words. UNKNOWN represents any input word
 * that doesn't match one of the others (i.e. an unrecognized command).
 */
public enum CommandType {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, BYE, UNKNOWN;

    /**
     * Returns true if this command changes the task list, and so requires the
     * tasks to be saved afterwards. Keeping this here means the answer lives
     * with the commands themselves, instead of being repeated at every place
     * that changes the list.
     */
    public boolean isTaskListChanged() {
        return this == MARK || this == UNMARK || this == DELETE
                || this == TODO || this == DEADLINE || this == EVENT;
    }
}
