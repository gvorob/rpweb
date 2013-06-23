/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author George
 */
public class TempStroke {
    Point start, end;
    long timeout;
    int size;
    Color c;
    
    public TempStroke(Color col, int width, int t, Point s, Point e)
    {
        timeout = t;
        c = col;
        start = s;
        end = e;
        size = width;
    }
    
    public void draw(DrawingManager d)
    {
        Graphics2D g = d.getTempGraphics2D();
        g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(c);
        g.drawLine(start.x, start.y, end.x, end.y);
    }
    
    public void subtractTime(long t)
    {
        timeout-=t;
    }
    
    public boolean isDead(){return timeout<0;}
}
