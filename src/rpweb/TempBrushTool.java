/*
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 * @author George
 */
public class TempBrushTool extends Tool{
    public static final int BYTES_IN_TRANSFER = 24;
    
    public int radius;
    public Color c;
    public Point mousePos;
    public Point last;
    public boolean lClick;
    public static int delay = 80;
    //public BufferedImage b;
    
    public TempBrushTool(Color col, int r)
    {
        radius = r;
        c = col;
        name = "TempBrushTool";
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
            if(last == null) last = mousePos;
            d.addStroke(new TempStroke(c, radius*2, delay, new Point(mousePos.x - radius,mousePos.y-radius), new Point(last.x - radius,last.y-radius)));
            byte[] temp = sendData(mousePos.x, mousePos.y,last.x,last.y);
            last = mousePos;
            return temp;
        }
        last = null;
        return new byte[0];
    }
    
    public static void processIn(byte[] input,DrawingManager d)
    {
        int x = byteUtils.byteToInt(input,0);
        int y = byteUtils.byteToInt(input,4);
        int x2 = byteUtils.byteToInt(input,8);
        int y2 = byteUtils.byteToInt(input,12);
        int rad = byteUtils.byteToInt(input,16);
        d.addStroke(new TempStroke(new Color(byteUtils.unsignedByte(input[20]),byteUtils.unsignedByte(input[21]),byteUtils.unsignedByte(input[22])), rad*2, delay, new Point(x-rad, y-rad),new Point(x2-rad, y2-rad)));
     }
    
    public byte[] sendData(int x, int y ,int x2, int y2)
    { 
        byte[] temp1 = byteUtils.getBytes(x);
        byte[] temp2 = byteUtils.getBytes(y);
        byte[] temp3 = byteUtils.getBytes(x2);
        byte[] temp4 = byteUtils.getBytes(y2);
        byte[] temp5 = byteUtils.getBytes(radius);
        byte[] col = new byte[]{(byte)c.getRed(),(byte)c.getGreen(),(byte)c.getBlue()};
        byte[] temp = new byte[BYTES_IN_TRANSFER];
        temp[0] = byteUtils.TEMP_BYTE;
        System.arraycopy(temp1, 0, temp, 1, 4);
        System.arraycopy(temp2, 0, temp, 5, 4);
        System.arraycopy(temp3, 0, temp, 9, 4);
        System.arraycopy(temp4, 0, temp, 13, 4);
        System.arraycopy(temp5, 0, temp, 17, 4);
        System.arraycopy(col, 0, temp, 21, 3);
        return temp;
    }
    
    public void remind(Reminder r,DrawingManager d)
    {
        if(r.remindParams.length!=3)
        {
            System.out.println("REMINDER ERROR IN TEMPBRUSHTOOL");
            return;
        }
        Graphics2D g = d.getTempGraphics2D();
        Color temp = new Color(0x00FFFFFF, true);
        g.setColor(temp);
        g.setComposite(AlphaComposite.Clear);
        g.fillOval(r.remindParams[0], r.remindParams[1], r.remindParams[2]*2, r.remindParams[2]*2);
    }
}
