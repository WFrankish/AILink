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

  /**
   * @param args an array of arguments to search
   * @param target a string to search for
   * @return the position of the target string, or -1
   */
  public static int find(String[] args, String target){
    for(int i = 0; i<args.length; i++){
      if(args[i].equals(target)){
        return i;
      }
    }
    return -1;
  }

  /**
   * @param args an array of arguments to search
   * @param mark a string that should indicate the next argument is the value
   * @param defaultVal the default value
   * @return the int following mark in args, or defaultVal if the value can't be found
   */
  public static int findVal(String[] args, String mark, int defaultVal){
    try{
      int pos = find(args, mark);
      return Integer.parseInt(args[pos + 1]);
    }
    catch(Exception e){
      return defaultVal;
    }
  }

  /**
   * @param val the value to clamp
   * @param min the minimum allowed value
   * @param max the maximum allowed value
   * @return min if val < min, max if val > max, val otherwise
   */
  public static int clamp(int val, int min, int max){
    return Math.min(max, Math.max(min, val));
  }

}
