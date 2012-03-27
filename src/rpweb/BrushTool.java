/*
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author George
 */
public class BrushTool extends Tool{
    public static final int BYTES_IN_TRANSFER = 16;
    
    public int radius;
    public Color c;
    public Point mousePos;
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
            Graphics g = d.getBaseGraphics();
            g.setColor(c);
            g.fillOval(mousePos.x-radius, mousePos.y-radius, radius*2, radius*2);
            return sendData(mousePos.x, mousePos.y);
        }
        return new byte[0];
    }
    
    public static void processIn(byte[] input, DrawingManager d)
    {
        Graphics g = d.getBaseGraphics();
        g.setColor(new Color((input[12]<<16)|(input[13]<<8)|input[14]));
        int x = byteUtils.byteToInt(input,0);
        int y = byteUtils.byteToInt(input,4);
        int rad = byteUtils.byteToInt(input,8);
        g.fillOval(x-rad, y-rad, rad*2, rad*2);
    }
    
    public byte[] sendData(int x, int y)
    { 
        byte[] temp1 = byteUtils.getBytes(x);
        //byteUtils.printBytes(temp1);
        byte[] temp2 = byteUtils.getBytes(y);
        byte[] temp3 = byteUtils.getBytes(radius);
        byte[] col = new byte[]{(byte)c.getRed(),(byte)c.getGreen(),(byte)c.getBlue()};
        byte[] temp = new byte[16];
        temp[0] = byteUtils.BRUSH_BYTE;
        System.arraycopy(temp1, 0, temp, 1, 4);
        System.arraycopy(temp2, 0, temp, 5, 4);
        System.arraycopy(temp3, 0, temp, 9, 4);
        System.arraycopy(col, 0, temp, 13, 3);
        return temp;
    }
    
}
