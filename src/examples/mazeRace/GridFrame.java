package examples.mazeRace;

import common.Coord;

import java.awt.*;

public class GridFrame {

  public GridFrame(int dimX, int dimY, Color bg){
    frame_ = new GridFrameP();
    frame_.setBackground(bg);
    frame_.setSize(5 * dimX + 5, 5 * dimY + 5);
    frame_.setResizable(false);
    frame_.setUndecorated(true);
    grid_ = new Color[dimX][dimY];
    for(int i = 0; i < grid_.length; i++){
      for(int j = 0; j < grid_[i].length; j++){
        grid_[i][j] = bg;
      }
    }
  }

  public void redraw(){
    if(frame_.isVisible()) {
        frame_.paint(frame_.getGraphics());
    }
  }

  public void setColour(Color colour, Coord loc){
    grid_[loc.x][loc.y] = colour;
  }

  public void makeVisible(boolean b){
    frame_.setVisible(b);
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

}
