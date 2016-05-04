package examples.mazeRace;

import java.awt.*;

public class GridFrame {

  public GridFrame(int dimX, int dimY){
    fast_ = true;
    frame_ = new GridFrameP();
    frame_.setBackground(Color.black);
    frame_.setSize(5 * dimX + 5, 5 * dimY + 5);
    frame_.setResizable(false);
    frame_.setUndecorated(true);
    frame_.setVisible(true);
    grid_ = new Color[dimX][dimY];
    for(int i = 0; i < grid_.length; i++){
      for(int j = 0; j < grid_[i].length; j++){
        grid_[i][j] = Color.black;
      }
    }
  }

  public void redraw(boolean force){
    if(force){
      frame_.repaint();
    }
    else if(!fast_) {
        frame_.paint(frame_.getGraphics());
    }
  }

  public void setColour(Color colour, int x, int y){
    grid_[x][y] = colour;
  }

  public void setIsFast(boolean fast){
    fast_ = fast;
  }

  private Color[][] grid_;
  private GridFrameP frame_;

  private class GridFrameP extends Frame{
    @Override
    public void paint(Graphics g) {
      for(int i = 0; i < grid_.length; i++){
        for(int j = 0; j < grid_[i].length; j++){
          g.setColor(grid_[i][j]);
          g.fillRect(5 * i+2, 5 * j+2, 4, 4);
        }
      }
    }
  }

  private boolean fast_;

}
