package debug;

import templates.*;

/**
 * A test implementation of an AILink program which is actually just a really convoluted echo server when given a
 * working implementation of AgentInterface and GameInterface.
 */
public class Echo {
  private class EchoGame implements Game{

  }

  public static class EchoActionMaster implements ActionMaster {
    @Override
    public Action[] parseActions(String input) {
      Action[] out = new Action[input.length()];
      for (int i = 0; i < input.length(); i++) {
        out[i] = new EchoAction(input.substring(i, i + 1));
      }
      return out;
    }

    @Override
    public Action parseAction(String input) {
      return new EchoAction(input);
    }
  }

  /**
   * An EchoAction is a string.
   */
  public static class EchoAction implements Action{
    public EchoAction(String str){
      msg_ = str;
    }

    @Override
    public String toString() {
      return msg_;
    }

    private String msg_;
  }

  public static class EchoStateMaster implements StateMaster{
    @Override
    public State parseString(String input) {
      return new EchoState(input);
    }
  }

  /**
   * The EchoState is all previous messages.
   */
  public static class EchoState implements State{
    public EchoState(String str){
      transcript_ = str;
    }

    @Override
    public String toString() {
      return transcript_;
    }

    private String transcript_;
  }
}
