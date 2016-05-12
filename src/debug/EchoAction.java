package debug;


import interfaces.Action;

/**
 * An EchoAction is simply a string.
 */
public class EchoAction implements Action {
  public EchoAction(String str){
    msg_ = str;
  }

  @Override
  public String toString() {
    return msg_;
  }

  @Override
  public String toReadable() {
    return msg_;
  }

  private String msg_;
}