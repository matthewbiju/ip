import java.time.LocalDate;

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

    /**
     * Returns what the task says, e.g. "return book".
     *
     * The field itself is protected, which lets subclasses use it but not
     * anything else once the classes sit in different packages. Whoever
     * displays a task needs the description, so it is offered here rather
     * than by widening the field.
     */
    public String getDescription() {
        return description;
    }

    public String getTypeIcon() {
        return " ";
    }

    public String getDetails() {
        return "";
    }

    /**
     * Returns true if this task falls on the given day.
     *
     * A plain task has no date, so it never falls on any particular day and
     * this returns false. Subclasses that do have dates override it. Asking
     * the task itself, rather than testing its type from outside, means a new
     * kind of task decides this for itself instead of every place that
     * searches by date needing to learn about it.
     */
    public boolean isOn(LocalDate date) {
        return false;
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
