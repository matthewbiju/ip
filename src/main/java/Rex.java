import java.util.Scanner;

public class Rex {
    public static void main(String[] args) {
        String banner = " ____  _______  __\n"
                + "|  _ \\| ____\\ \\/ /\n"
                + "| |_) |  _|  \\  / \n"
                + "|  _ <| |___ /  \\ \n"
                + "|_| \\_\\_____/_/\\_\\\n";
        System.out.println(banner);
        System.out.println("Hello! I'm Rex!");
        System.out.println("What can I do for you?");

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("bye")) {
            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + taskLine(tasks[i]));
                }
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                tasks[index].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + taskLine(tasks[index]));
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                tasks[index].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + taskLine(tasks[index]));
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5);
                tasks[taskCount] = new ToDo(description);
                taskCount++;
                printAddedConfirmation(tasks[taskCount - 1], taskCount);
            } else if (input.startsWith("deadline ")) {
                String[] parts = input.substring(9).split(" /by ", 2);
                tasks[taskCount] = new Deadline(parts[0], parts[1]);
                taskCount++;
                printAddedConfirmation(tasks[taskCount - 1], taskCount);
            } else if (input.startsWith("event ")) {
                String[] fromParts = input.substring(6).split(" /from ", 2);
                String[] toParts = fromParts[1].split(" /to ", 2);
                tasks[taskCount] = new Event(fromParts[0], toParts[0], toParts[1]);
                taskCount++;
                printAddedConfirmation(tasks[taskCount - 1], taskCount);
            }
            input = scanner.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
        scanner.close();
    }

    private static String taskLine(Task task) {
        return "[" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task.description + task.getDetails();
    }

    private static void printAddedConfirmation(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + taskLine(task));
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
