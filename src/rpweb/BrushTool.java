/*
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 * @author George
 */
public class BrushTool extends Tool{
    public static final int BYTES_IN_TRANSFER = 24;
    
    public int radius;
    public Color c;
    public Point mousePos;
    public Point last;
    public boolean lClick;
    //public BufferedImage b;
    
    public BrushTool(Color col, int r)
    {
        radius = r;
        c = col;
        name = "BrushTool";
    }
    
    public void mouseEvent(MouseEvent e, boolean pressed,boolean released)
    {
        mousePos = e.getPoint();
        if(e.getButton()== MouseEvent.BUTTON1)
        {
            if(pressed)
                lClick = true;
            if(released)
                lClick = false;
        }
        //System.out.println(flag);
    }
    
    public byte[] update(DrawingManager d)
    {
        if(lClick)
        {
            if(last == null)last = mousePos;
            Graphics2D g = d.getBaseGraphics2D();
            g.setColor(c);
            g.setStroke(new BasicStroke(radius*2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(mousePos.x-radius, mousePos.y-radius, last.x-radius,last.y-radius);
            byte[] temp  = sendData(mousePos.x, mousePos.y, last.x, last.y);
            last = mousePos;
            return temp;
        }
        last = null;
        return new byte[0];
    }
    
    public static void processIn(byte[] input, DrawingManager d)
    {
        Graphics2D g = d.getBaseGraphics2D();
        g.setColor(new Color((input[20]<<16)|(input[21]<<8)|input[22]));
        int x = byteUtils.byteToInt(input,0);
        int y = byteUtils.byteToInt(input,4);
        int lx = byteUtils.byteToInt(input,8);
        int ly = byteUtils.byteToInt(input,12);        
        int rad = byteUtils.byteToInt(input,16);
        g.setStroke(new BasicStroke(rad*2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x-rad, y-rad, lx-rad,ly-rad);
    }
    
    public byte[] sendData(int x, int y, int lx, int ly)
    { 
        byte[] temp1 = byteUtils.getBytes(x);
        //byteUtils.printBytes(temp1);
        byte[] temp2 = byteUtils.getBytes(y);
        byte[] temp3 = byteUtils.getBytes(lx);
        byte[] temp4 = byteUtils.getBytes(ly);
        byte[] temp5 = byteUtils.getBytes(radius);
        byte[] col = new byte[]{(byte)c.getRed(),(byte)c.getGreen(),(byte)c.getBlue()};
        byte[] temp = new byte[BYTES_IN_TRANSFER];
        temp[0] = byteUtils.BRUSH_BYTE;
        System.arraycopy(temp1, 0, temp, 1, 4);
        System.arraycopy(temp2, 0, temp, 5, 4);
        System.arraycopy(temp3, 0, temp, 9, 4);
        System.arraycopy(temp4, 0, temp, 13, 4);
        System.arraycopy(temp5, 0, temp, 17, 4);
        System.arraycopy(col, 0, temp, 21, 3);
        return temp;
    }
    
}
