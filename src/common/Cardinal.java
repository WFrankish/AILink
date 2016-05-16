package common;


public enum Cardinal {
  NORTH, EAST, SOUTH, WEST;

  public Cardinal opposite(){
    switch (this){
      case NORTH:{
        return SOUTH;
      }
      case EAST:{
        return WEST;
      }
      case SOUTH:{
        return NORTH;
      }
      default:{
        return EAST;
      }
    }
  }

  public String toReadable() {
    switch (this) {
      case NORTH: {
        return "North";
      }
      case EAST: {
        return "East";
      }
      case SOUTH: {
        return "South";
      }
      default: {
        return "West";
      }
    }
  }

  public String toString() {
    switch (this) {
      case NORTH: {
        return "N";
      }
      case EAST: {
        return "E";
      }
      case SOUTH: {
        return "S";
      }
      default: {
        return "W";
      }
    }
  }

  public static Cardinal parse(char input){
    switch (input){
      case 'N':{
        return NORTH;
      }
      case 'E':{
        return EAST;
      }
      case 'S':{
        return SOUTH;
      }
      case 'W':{
        return WEST;
      }
      default:{
        return null;
      }
    }
  }

}
