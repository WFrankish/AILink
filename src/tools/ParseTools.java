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

  public static int find(String[] args, String target){
    for(int i = 0; i<args.length; i++){
      if(args[i].equals(target)){
        return i;
      }
    }
    return -1;
  }

  public static int findVal(String[] args, String mark, int defaultVal){
    try{
      int pos = find(args, mark);
      return Integer.parseInt(args[pos + 1]);
    }
    catch(Exception e){
      return defaultVal;
    }
  }

  public static int clamp(int val, int min, int max){
    return Math.min(max, Math.max(min, val));
  }


}
