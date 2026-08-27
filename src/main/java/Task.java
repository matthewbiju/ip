public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public String getTypeIcon() {
        return " ";
    }

    public String getDetails() {
        return "";
    }

    /**
     * Returns this task as a single line in the save file format, e.g.
     * "T | 1 | read book", where the second field is 1 if the task is done
     * and 0 if it is not. Subclasses append their own extra fields to this.
     *
     * Note that a description containing " | " would produce a line that
     * cannot be read back correctly. Descriptions like that are rare enough
     * that this is left unhandled rather than escaped.
     */
    public String toSaveFormat() {

        return getTypeIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }
}
