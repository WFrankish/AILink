package examples.twentyOne;


import templates.Action;
import templates.ActionMaster;

public class tw1ActionMaster implements ActionMaster{

  @Override
  public String actionsToString(Action[] actions) {
    if(actions.length > 1){
      return "dh";
    }
    else{
      return actions[0].toString();
    }
  }

  @Override
  public String actionsToReadable(Action[] actions) {
    if(actions.length > 1){
      return "Draw or Hit";
    }
    else{
      return actions[0].toReadable();
    }
  }

  @Override
  public Action parseAction(String input){
    if(input.equals("d")){
      return new tw1Action.Draw();
    }
    else{
      return new tw1Action.Hit();
    }
  }

  @Override
  public Action[] parseActions(String input) {
    if(input.length()>1) {
      Action[] actions = new Action[2];
      actions[0] = new tw1Action.Draw();
      actions[1] = new tw1Action.Hit();
      return actions;
    }
    else{
      Action[] actions = new Action[1];
      actions[0] = parseAction(input);
      return actions;
    }
  }

}
