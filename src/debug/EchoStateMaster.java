package debug;

import interfaces.State;
import interfaces.StateMaster;

/**
 * Must be able to parse Transcripts and AllowedChars
 */
public class EchoStateMaster implements StateMaster {
  @Override
  public State parseString(String input) {
    if(input.length() == 0){
      return null;
    } else {
      char header = input.charAt(0);
      switch (header){
        case 'A':{
          char[] allowed;
          if(input.length() == 1){
            allowed = new char[0];
          } else {
            allowed = input.substring(1).toCharArray();
          }
          return new EchoState.AllowedChars(allowed);
        }
        case 'T':{
          String transcript;
          if(input.length() == 1){
            transcript = "";
          } else {
            transcript = input.substring(1);
          }
          return new EchoState.Transcript(transcript);
        }
        default:{
          return null;
        }
      }
    }

  }

}