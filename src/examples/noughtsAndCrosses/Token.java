package examples.noughtsAndCrosses;

public class Token {

  public static Token nought(){
    return new Token(1);
  }

  public static Token cross(){
    return new Token(2);
  }

  public static Token blank(){
    return new Token(0);
  }

  public  boolean isNought(){
      return  me_ == 1;
    }

  public  boolean isCross(){
      return me_ == 2;
    }

  public boolean equals(Object o){
    if(o instanceof Token){
      Token that = (Token) o;
      return this.isNought() == that.isNought() && this.isCross() == that.isCross();
    }
    else{
      return false;
    }
  }

  public String toString(){
    if(me_==1){
      return "Nought";
    }
    else if(me_==2){
      return "Cross";
    }
    else{
      return "Blank";
    }
  }

  private int me_ = 0;

  private Token(int me){
    me_ = me;
  }

}
