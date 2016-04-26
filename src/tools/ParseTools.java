package tools;

public class ParseTools {

  /**
   * parses strings beginning with t, T, y, Y or - followed by aforementioned characters as true, otherwise false.
   * @param str string to parse
   * @return whether string is parsed as True
   */
  public static boolean parseTruth(String str){
    if(str.length()==0){
      return false;
    }
    char decider;
    if(str.charAt(0)=='-'){
      if(str.length()==1){
        return false;
      }
      else{
        decider = str.charAt(1);
      }
    }
    else{
      decider = str.charAt(0);
    }
    return (decider == 'y' || decider == 'Y' || decider == 't' || decider == 'T');
  }
}
