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

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
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

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 1 tasks in your bowl!
>>> deadline return book /by Sunday
Got it! I've fetched this task for you:
  [D][ ] return book (by: Sunday)
You now have 2 tasks in your bowl!
>>> event project meeting /from Mon 2pm /to 4pm
Got it! I've fetched this task for you:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
You now have 3 tasks in your bowl!
>>> list
Here's what's in your bowl:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
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

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 1 tasks in your bowl!
>>> deadline return book /by Sunday
Got it! I've fetched this task for you:
  [D][ ] return book (by: Sunday)
You now have 2 tasks in your bowl!
>>> mark 1
Nice catch! I've marked this task as done:
  [T][X] borrow book
>>> unmark 1
Okay, putting this one back in the yard — not done yet:
  [T][ ] borrow book
>>> list
Here's what's in your bowl:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC4: Error handling

**Aim:** Verify bad input produces a clear error (via `RexException`) instead
of being silently ignored or crashing the program, and that the program
keeps running afterward.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo
OOPS!!! Ruff! The description of a todo cannot be empty.
>>> blah
OOPS!!! Woof? I don't know what that means :-(
>>> deadline return book
OOPS!!! Ruff! A deadline needs a '/by' date, e.g. deadline return book /by Sunday.
>>> mark abc
OOPS!!! Woof! "abc" isn't a valid task number.
>>> mark 99
OOPS!!! Woof! There's no task numbered 99 in your bowl.
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 1 tasks in your bowl!
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```

### TC5: Delete tasks

**Aim:** Verify `delete` removes the correct task, reports it back, updates
the task count, and that `list` reflects the new indices. Also verify
`delete` reuses the same task-number validation as `mark`/`unmark`.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Woof woof! I'm Rex, your task-fetching sidekick!
What can I fetch for you today?
>>> todo read book
Got it! I've fetched this task for you:
  [T][ ] read book
You now have 1 tasks in your bowl!
>>> deadline return book /by Sunday
Got it! I've fetched this task for you:
  [D][ ] return book (by: Sunday)
You now have 2 tasks in your bowl!
>>> todo borrow book
Got it! I've fetched this task for you:
  [T][ ] borrow book
You now have 3 tasks in your bowl!
>>> delete 2
Gotcha! I've removed this task from your bowl:
  [D][ ] return book (by: Sunday)
You now have 2 tasks in your bowl!
>>> list
Here's what's in your bowl:
1.[T][ ] read book
2.[T][ ] borrow book
>>> delete 99
OOPS!!! Woof! There's no task numbered 99 in your bowl.
>>> bye
Bye! *wags tail* Hope to fetch for you again soon!
```
