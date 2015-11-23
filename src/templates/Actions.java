package templates;

import templates.Action;

/**
 * A set of all actions that may become available as part of an AI problem.
 */
public interface Actions {
  Action[] parseString(String input);
}
