import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the task list to a file on disk and loads it back, so that tasks
 * survive between runs of the program. This is the only class that knows
 * where the tasks are kept or what the saved file looks like; the rest of
 * the program just hands it a list of tasks.
 */
public class Storage {
    private final Path file;

    /**
     * Creates a Storage that reads and writes the given file.
     *
     * @param first the first part of the path to the save file, relative to
     *     the folder the program is run from (e.g. "data").
     * @param more the remaining parts of the path (e.g. "rex.txt"). The path
     *     is assembled from separate parts rather than a single string so
     *     that it works on any operating system, instead of hard-coding a
     *     separator such as "/" or "\".
     */
    public Storage(String first, String... more) {

        this.file = Paths.get(first, more);
    }

    /**
     * Writes the given tasks to the save file, replacing anything already in
     * it. The containing folder is created first if it does not exist yet.
     *
     * @param tasks the tasks to save, in the order they should be restored.
     * @throws IOException if the file could not be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path folder = file.getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toSaveFormat());
        }
        Files.write(file, lines);
    }

    /** Returns the path of the save file, for use in messages to the user. */
    public Path getFile() {
        return file;
    }
}
