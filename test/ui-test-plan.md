# UI Test Plan

Regression test cases for Rex's console interface, run via the `test-ui` skill
(`.claude/skills/test-ui/`). Each test case is one continuous program session:
inputs are sent in order, and the program's output is checked against the
expected output after each input.

## How to run

Invoke the `test-ui` skill, or run directly from the repository root:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

## Format

Each test case is a fenced ` ```session ` block. A line starting with `>>> `
sends the rest of that line as one line of input (or, if it is exactly
`(startup)`, sends nothing and just captures the program's output before any
input is read). All following lines up to the next `>>> ` (or the end of the
block) are the expected output for that step, compared line for line.
Trailing whitespace on each line is ignored; everything else must match
exactly.

## Test cases

### TC1: Greeting and exit

**Aim:** Verify the startup banner/greeting print correctly, and that `bye`
exits with the farewell message.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Hello! I'm Rex!
What can I do for you?
>>> bye
Bye. Hope to see you again soon!
```

### TC2: Add todos, deadlines, and events; list them

**Aim:** Verify all three task types can be added via `todo`/`deadline`/
`event`, each with the correct type icon, confirmation message, and task
count, and that `list` renders each type's details correctly.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Hello! I'm Rex!
What can I do for you?
>>> todo borrow book
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
>>> deadline return book /by Sunday
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
>>> event project meeting /from Mon 2pm /to 4pm
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
>>> list
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
>>> bye
Bye. Hope to see you again soon!
```

### TC3: Mark and unmark tasks

**Aim:** Verify `mark`/`unmark` update a task's status icon (alongside its
type icon), and that `list` reflects the change.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Hello! I'm Rex!
What can I do for you?
>>> todo borrow book
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
>>> deadline return book /by Sunday
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
>>> mark 1
Nice! I've marked this task as done:
  [T][X] borrow book
>>> unmark 1
OK, I've marked this task as not done yet:
  [T][ ] borrow book
>>> list
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
>>> bye
Bye. Hope to see you again soon!
```
