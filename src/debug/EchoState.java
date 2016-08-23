package debug;

import interfaces.State;

public class EchoState {

  /**
   * A set of allowed chars.
   */
  public static class AllowedChars implements State {

    public AllowedChars(char[] allowed){
      allowed_ = allowed;
    }

    @Override
    public String encode() {
      StringBuilder result = new StringBuilder();
      for(char c : allowed_){
        result.append(c);
      }
      return 'A' + result.toString();
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
      for(char c : allowed_){
        if(c == ' '){
          // print ' ' as "space" for readability
          result.append("space");
        } else {
          result.append(c);
        }
        result.append(' ');
      }
      return result.toString();
    }

    public char[] getAllowed(){
      return allowed_;
    }

    private char[] allowed_;
  }

  /**
   * A log of all previous messages.
   */
  public static class Transcript implements State {
    public Transcript(String str){
      transcript_ = new StringBuilder();
      append(str);
    }

    public void append(String str){
      transcript_.append(str);
    }

    @Override
    public String encode() {
      return ('T' + transcript_.toString());
    }

    @Override
    public String toString() {
      return transcript_.toString();
    }

    private StringBuilder transcript_;
  }
}
