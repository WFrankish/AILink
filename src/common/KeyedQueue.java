package common;

import java.util.ArrayList;

public class KeyedQueue<T> {

  public boolean isEmpty(){
    return queue_.isEmpty();
  }

  public void add(int k, T t){
    int i = 0;
    boolean found = false;
    while(i<keys_.size() && !found){
      int cur = keys_.get(i);
      if(k <= cur){
        found = true;
      } else {
        i++;
      }
    }
    keys_.add(i, k);
    queue_.add(i, t);
  }

  public T pop(){
    keys_.remove(0);
    return queue_.remove(0);
  }


  private ArrayList<T> queue_ = new ArrayList<T>();
  private ArrayList<Integer> keys_ = new ArrayList<Integer>();
}
