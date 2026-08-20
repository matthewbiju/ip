/**
 * The set of recognized command words. UNKNOWN represents any input word
 * that doesn't match one of the others (i.e. an unrecognized command).
 */
public enum CommandType {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, BYE, UNKNOWN
}
