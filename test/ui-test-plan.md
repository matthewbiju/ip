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

### TC2: Add and list tasks

**Aim:** Verify tasks can be added and are listed back, numbered, with an
unset status icon.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Hello! I'm Rex!
What can I do for you?
>>> read book
added: read book
>>> return book
added: return book
>>> list
Here are the tasks in your list:
1.[ ] read book
2.[ ] return book
>>> bye
Bye. Hope to see you again soon!
```

### TC3: Mark and unmark tasks

**Aim:** Verify `mark`/`unmark` update a task's status icon, and that `list`
reflects the change.

```session
>>> (startup)
 ____  _______  __
|  _ \| ____\ \/ /
| |_) |  _|  \  / 
|  _ <| |___ /  \ 
|_| \_\_____/_/\_\

Hello! I'm Rex!
What can I do for you?
>>> read book
added: read book
>>> return book
added: return book
>>> mark 1
Nice! I've marked this task as done:
  [X] read book
>>> unmark 1
OK, I've marked this task as not done yet:
  [ ] read book
>>> list
Here are the tasks in your list:
1.[ ] read book
2.[ ] return book
>>> bye
Bye. Hope to see you again soon!
```
