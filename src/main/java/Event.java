/**
 * A task that spans a stretch of time, from one date to another. Either end
 * may carry a time of day, so an event can be a meeting on one afternoon or a
 * trip lasting several days.
 */
public class Event extends Task {
    protected TaskDateTime from;
    protected TaskDateTime to;

    /**
     * Creates an event.
     *
     * @param description what is happening.
     * @param from when it starts.
     * @param to when it ends.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * Note that this writes both dates in their save format, not the one shown
     * to the user, so that the line can be read back by TaskDateTime.parse.
     */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + from.toSaveFormat() + " | " + to.toSaveFormat();
    }
}
