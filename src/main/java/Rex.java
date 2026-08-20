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

        String[] tasks = new String[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("bye")) {
            if (input.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("added: " + input);
            }
            input = scanner.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
        scanner.close();
    }
}
