/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author George
 */
public class DrawingManager implements MouseInputListener {
    //
    private BufferedImage baseLayer;
    private BufferedImage tempLayer;
    private ToolBelt tools;
    private Canvas drawing;
    private JPanel subToolInfo;
    private boolean shouldRedraw;
    private ArrayList<Reminder> reminders;
    private long lastTime;
    private ArrayList<TempStroke> strokes;
    
    public DrawingManager(Canvas c)
    {
        reminders = new ArrayList<Reminder>(0);
        baseLayer = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        tempLayer = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = baseLayer.getGraphics();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        tools = new ToolBelt();
        tools.add(new BrushTool(Color.red,5));
        tools.add(new EraseTool(24));
        tools.add(new TempBrushTool(Color.blue, 5));
        drawing = c;
        subToolInfo = new JPanel();
        strokes = new ArrayList<TempStroke>(0);
    }
    
    public JPanel getToolSelectPanel()
    {
        return subToolInfo;
    }
    
    public void addStroke(TempStroke temp)
    {
        strokes.add(temp);
    }
    
    public void tick(long time)
    {
        Iterator<Reminder> iter = reminders.iterator();
        while(iter.hasNext())
        {
            Reminder temp = iter.next();
            temp.delay-=time;
            if(temp.delay <= 0)
            {
                temp.sender.remind(temp, this);
                iter.remove();
            }
        }
        Iterator<TempStroke> iter2 = strokes.iterator();
        while(iter2.hasNext())
        {
            TempStroke temp = iter2.next();
            temp.subtractTime(time);
            if(temp.isDead())
            {
                iter2.remove();
            }
        }
    }
    
    public byte[] update()
    {
        tick(System.currentTimeMillis() - lastTime);
        lastTime = System.currentTimeMillis();
        shouldRedraw = false;
        if(tools.getRedraw())
        {
            subToolInfo.removeAll();
            subToolInfo.add(tools.getSelectPanel());
            shouldRedraw = true;
        }
        return tools.update(this);
    }
    
    public void remindMe(Reminder r)
    {
         reminders.add(r);
    }
    
    public boolean isRedraw()
    {
        return shouldRedraw;
    }
    
    public void draw()
    {
        Iterator<TempStroke> iter = strokes.iterator();
        while(iter.hasNext())
        {
            iter.next().draw(this);
        }
        Graphics g = baseLayer.getGraphics();
        g.drawRect(0, 0, drawing.getWidth()-1, drawing.getHeight()-1);
        BufferedImage b = new BufferedImage(drawing.getWidth(),drawing.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
        g = b.getGraphics();
        g.drawImage(baseLayer, 0, 0, null);
        g.drawImage(tempLayer, 0, 0, null);
        g = drawing.getGraphics();
        g.drawImage(b, 0, 0, null);
        tempLayer = new BufferedImage(drawing.getWidth(), drawing.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    }
    
    public int processIn(byte[] input, int i, int user)//Not used yet? perhaps later things
    {
        if(input[i] == byteUtils.ERASER_BYTE)
        {
            i++;
            byte[] tempIn = new byte[EraseTool.BYTES_IN_TRANSFER-1];
            for (int j = 0;j<EraseTool.BYTES_IN_TRANSFER-1;j++)
            {
                tempIn[j] = input[i];
                i++;
            }
            
            EraseTool.processIn(tempIn,this);
            return EraseTool.BYTES_IN_TRANSFER; 
        }
        if(input[i] == byteUtils.BRUSH_BYTE)
        {
            i++;
            byte[] tempIn = new byte[BrushTool.BYTES_IN_TRANSFER-1];
            for (int j = 0;j<BrushTool.BYTES_IN_TRANSFER-1;j++)
            {
                tempIn[j] = input[i];
                i++;
            }
            
            BrushTool.processIn(tempIn,this);
            return BrushTool.BYTES_IN_TRANSFER; 
        }
        System.out.println("TOOL FLAG "+input[i]+" NOT RECOGNIZED");
        return -1;
    }
    
    public Graphics getBaseGraphics()
    {
        return baseLayer.getGraphics();
    }
    
    public Graphics2D getBaseGraphics2D()
    {
        return baseLayer.createGraphics();
    }
    
    public Graphics getTempGraphics()
    {
        return tempLayer.getGraphics();
    }
    
    public Graphics2D getTempGraphics2D()
    {
        return tempLayer.createGraphics();
    }
    
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        tools.mouseEvent(e, true,false);
    }
    public void mouseReleased(MouseEvent e) {
        tools.mouseEvent(e, false,true);
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        tools.mouseEvent(e, false,false);
    }
    public void mouseMoved(MouseEvent e) {
        tools.mouseEvent(e, false,false);
    }
}
