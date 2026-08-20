import java.util.ArrayList;
import java.util.Scanner;

public class Rex {
    public static void main(String[] args) {
        String banner = " ____  _______  __\n"
                + "|  _ \\| ____\\ \\/ /\n"
                + "| |_) |  _|  \\  / \n"
                + "|  _ <| |___ /  \\ \n"
                + "|_| \\_\\_____/_/\\_\\\n";
        System.out.println(banner);
        System.out.println("Woof woof! I'm Rex, your task-fetching sidekick!");
        System.out.println("What can I fetch for you today?");

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("bye")) {
            try {
                if (input.equals("list")) {
                    System.out.println("Here's what's in your bowl:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + taskLine(tasks.get(i)));
                    }
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    String argument = input.length() > 4 ? input.substring(5) : "";
                    int index = parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsDone();
                    System.out.println("Nice catch! I've marked this task as done:");
                    System.out.println("  " + taskLine(tasks.get(index)));
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    String argument = input.length() > 6 ? input.substring(7) : "";
                    int index = parseTaskIndex(argument, tasks.size());
                    tasks.get(index).markAsNotDone();
                    System.out.println("Okay, putting this one back in the yard — not done yet:");
                    System.out.println("  " + taskLine(tasks.get(index)));
                } else if (input.equals("delete") || input.startsWith("delete ")) {
                    String argument = input.length() > 6 ? input.substring(7) : "";
                    int index = parseTaskIndex(argument, tasks.size());
                    Task removed = tasks.remove(index);
                    System.out.println("Gotcha! I've removed this task from your bowl:");
                    System.out.println("  " + taskLine(removed));
                    System.out.println("You now have " + tasks.size() + " tasks in your bowl!");
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = (input.length() > 4 ? input.substring(5) : "").trim();
                    if (description.isEmpty()) {
                        throw new RexException("Ruff! The description of a todo cannot be empty.");
                    }
                    tasks.add(new ToDo(description));
                    printAddedConfirmation(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    String rest = input.length() > 8 ? input.substring(9) : "";
                    String[] parts = rest.split(" /by ", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new RexException("Ruff! The description of a deadline cannot be empty.");
                    }
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new RexException("Ruff! A deadline needs a '/by' date, "
                                + "e.g. deadline return book /by Sunday.");
                    }
                    tasks.add(new Deadline(description, parts[1].trim()));
                    printAddedConfirmation(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.equals("event") || input.startsWith("event ")) {
                    String rest = input.length() > 5 ? input.substring(6) : "";
                    String[] fromParts = rest.split(" /from ", 2);
                    String description = fromParts[0].trim();
                    if (description.isEmpty()) {
                        throw new RexException("Ruff! The description of an event cannot be empty.");
                    }
                    if (fromParts.length < 2 || fromParts[1].trim().isEmpty()) {
                        throw new RexException("Ruff! An event needs a '/from' time, "
                                + "e.g. event project meeting /from Mon 2pm /to 4pm.");
                    }
                    String[] toParts = fromParts[1].split(" /to ", 2);
                    if (toParts.length < 2 || toParts[1].trim().isEmpty()) {
                        throw new RexException("Ruff! An event needs a '/to' time, "
                                + "e.g. event project meeting /from Mon 2pm /to 4pm.");
                    }
                    tasks.add(new Event(description, toParts[0].trim(), toParts[1].trim()));
                    printAddedConfirmation(tasks.get(tasks.size() - 1), tasks.size());
                } else {
                    throw new RexException("Woof? I don't know what that means :-(");
                }
            } catch (RexException e) {
                System.out.println("OOPS!!! " + e.getMessage());
            }
            input = scanner.nextLine();
        }
        System.out.println("Bye! *wags tail* Hope to fetch for you again soon!");
        scanner.close();
    }

    /**
     * Parses a mark/unmark argument into a 0-based task index, throwing a
     * RexException (rather than letting NumberFormatException or an
     * out-of-range index propagate) if it isn't a valid task number.
     */
    private static int parseTaskIndex(String argument, int taskCount) throws RexException {
        int index;
        try {
            index = Integer.parseInt(argument.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new RexException("Woof! \"" + argument.trim() + "\" isn't a valid task number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new RexException("Woof! There's no task numbered " + argument.trim() + " in your bowl.");
        }
        return index;
    }

    private static String taskLine(Task task) {
        return "[" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task.description + task.getDetails();
    }

    private static void printAddedConfirmation(Task task, int taskCount) {
        System.out.println("Got it! I've fetched this task for you:");
        System.out.println("  " + taskLine(task));
        System.out.println("You now have " + taskCount + " tasks in your bowl!");
    }
}
