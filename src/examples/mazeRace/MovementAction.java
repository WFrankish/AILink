package examples.mazeRace;

import templates.Action;

public class MovementAction implements Action {

  public static MovementAction North = new MovementAction("North");

  public static MovementAction East = new MovementAction("East");

  public static MovementAction South = new MovementAction("South");

  public static MovementAction West = new MovementAction("West");

  @Override
  public String toString() {
    return direction_.substring(0,1);
  }

  @Override
  public String toReadable() {
    return direction_;
  }

  private MovementAction(String dir){
    direction_ = dir;
  }

  private String direction_;
}
