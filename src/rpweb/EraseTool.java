/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author George
 */
public class EraseTool extends Tool{
    public static final int BYTES_IN_TRANSFER = 13;
    
    public int radius;
    public Point mousePos;
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
            Graphics g = d.getBaseGraphics();
            g.fillOval(mousePos.x-radius, mousePos.y-radius, radius*2, radius*2);
            return sendData(mousePos.x, mousePos.y);
        }
        return new byte[0];
    }
    
    public static void processIn(byte[] input, DrawingManager d)
    {
        Graphics g = d.getBaseGraphics();
        int x = byteUtils.byteToInt(input,0);
        int y = byteUtils.byteToInt(input,4);
        int rad = byteUtils.byteToInt(input,8);
        
        g.fillOval(x-rad, y-rad, rad*2, rad*2);
    }
    
    public byte[] sendData(int x, int y)
    { 
        byte[] temp1 = byteUtils.getBytes(x);
        byte[] temp2 = byteUtils.getBytes(y);
        byte[] temp3 = byteUtils.getBytes(radius);
        byte[] temp = new byte[BYTES_IN_TRANSFER];
        temp[0] = byteUtils.ERASER_BYTE;
        System.arraycopy(temp1, 0, temp, 1, 4);
        System.arraycopy(temp2, 0, temp, 5, 4);
        System.arraycopy(temp3, 0, temp, 9, 4);
        return temp;
    }
}
