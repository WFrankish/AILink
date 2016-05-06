package common;

public class Tuple<S, T> {

  public Tuple(S first, T second){
    fst = first;
    snd = second;
  }

  @Override
  public String toString() {
    return "(" + fst.toString() + ", " + snd.toString() + ")";
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if(obj instanceof Tuple){
      Tuple<Object, Object> that = (Tuple<Object, Object>) obj;
      result = this.fst.equals(that.fst) && this.snd.equals(that.fst);
    }
    return result;
  }

  public S fst;
  public T snd;
}
