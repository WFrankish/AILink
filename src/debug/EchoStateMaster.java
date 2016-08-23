package debug;

import interfaces.State;
import interfaces.StateMaster;

/**
 * Must be able to parse Transcripts and AllowedChars
 */
public class EchoStateMaster implements StateMaster {
  @Override
  public State decode(String input) {
    if(input.length() == 0){
      return null;
    } else {
      char header = input.charAt(0);
      String content = input.length() == 1 ? "" : input.substring(1);
      switch (header){
        case 'A':{
          char[] allowed = content.toCharArray();
          return new EchoState.AllowedChars(allowed);
        }
        case 'T':{
          return new EchoState.Transcript(content);
        }
        default:{
          return null;
        }
      }
    }

  }

}