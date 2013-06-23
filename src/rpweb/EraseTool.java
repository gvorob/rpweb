/*
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
import java.awt.image.BufferedImage;

/**
 *
 * @author George
 */
public class EraseTool extends Tool{
    public static final int BYTES_IN_TRANSFER = 21;
    
    public int radius;
    public Point mousePos, last;
    public boolean lClick;
    //public BufferedImage b;
    
    public EraseTool(int r)
    {
        radius = r;
        name = "Eraser";
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
    }
    
    public byte[] update(DrawingManager d)
    {
        if(lClick)
        {
            if(last == null)last = mousePos;
            Graphics2D g = d.getBaseGraphics2D();
            g.setStroke(new BasicStroke(radius*2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(mousePos.x-radius, mousePos.y-radius, last.x-radius,last.y-radius);
            byte[] temp = sendData(mousePos.x, mousePos.y, last.x, last.y);
            last = mousePos;
            return temp;
        }
        last = null;
        return new byte[0];
    }
    
    public static void processIn(byte[] input, DrawingManager d)
    {
        Graphics2D g = d.getBaseGraphics2D();
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
        byte[] temp2 = byteUtils.getBytes(y);
        byte[] temp3 = byteUtils.getBytes(lx);
        byte[] temp4 = byteUtils.getBytes(ly);
        byte[] temp5 = byteUtils.getBytes(radius);
        byte[] temp = new byte[BYTES_IN_TRANSFER];
        temp[0] = byteUtils.ERASER_BYTE;
        System.arraycopy(temp1, 0, temp, 1, 4);
        System.arraycopy(temp2, 0, temp, 5, 4);
        System.arraycopy(temp3, 0, temp, 9, 4);
        System.arraycopy(temp4, 0, temp, 13, 4);
        System.arraycopy(temp5, 0, temp, 17, 4);
        return temp;
    }
}
