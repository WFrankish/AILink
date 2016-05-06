package examples.mazeRace;

import interfaces.State;
import interfaces.StateMaster;

public class MazeStateMaster implements StateMaster {

  @Override
  public State parseString(String input) {
    char indicator = input.charAt(0);
    switch(indicator){
      case 'D':{
        String[] strings = input.substring(1).split(",");
        int x = Integer.parseInt(strings[0]);
        int y = Integer.parseInt(strings[1]);
        return new MazeState.Dimension(x, y);
      }
      case 'S':{
        System.out.println(input);
        int i = 1;
        boolean opN = input.charAt(i) == 'o';
        String temp = "";
        while(Character.isDigit(input.charAt(i+1))){
          temp += input.charAt(i+1);
          i++;
        }
        int distN = Integer.parseInt(temp);
        i++;
        boolean opE = input.charAt(i) == 'o';
        temp = "";
        while(Character.isDigit(input.charAt(i+1))){
          temp += input.charAt(i+1);
          i++;
        }
        int distE = Integer.parseInt(temp);
        i++;
        boolean opS = input.charAt(i) == 'o';
        temp = "";
        while(Character.isDigit(input.charAt(i+1))){
          temp += input.charAt(i+1);
          i++;
        }
        int distS = Integer.parseInt(temp);
        i++;
        boolean opW = input.charAt(i) == 'o';
        temp = "";
        while(i+1 < input.length()){
          temp += input.charAt(i+1);
          i++;
        }
        int distW = Integer.parseInt(temp);
        return new MazeState.Sight(opN, distN, opE, distE, opS, distS, opW, distW);
      }
      case 'W':{
        return new MazeState.Winner(input.substring(1));
      }
      default:{
        return null;
      }
    }
  }

  @Override
  public String toString() {
    return "";
  }
}

