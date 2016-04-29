package examples.twentyOne;

import templates.Action;


public class tw1Action {

  public static class Draw implements Action{

    @Override
    public String toString() {
      return "d";
    }

    @Override
    public String toReadable() {
      return "Draw";
    }
  }

  public static class Hit implements Action{

    @Override
    public String toString() {
      return "h";
    }

    @Override
    public String toReadable() {
      return "Hit";
    }
  }
}
