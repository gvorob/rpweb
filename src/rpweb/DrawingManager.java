/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author George
 */
public class DrawingManager implements MouseInputListener {
    private BufferedImage baseLayer;
    private BufferedImage tempLayer;
    private ToolBelt tools;
    private Canvas drawing;
    private JPanel subToolInfo;
    private boolean shouldRedraw;
    
    public DrawingManager(Canvas c)
    {
        baseLayer = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        tempLayer = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = tempLayer.getGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        tools = new ToolBelt();
        tools.add(new BrushTool(Color.red,5));
        tools.add(new EraseTool(24));
        drawing = c;
        subToolInfo = new JPanel();
    }
    
    public JPanel getToolSelectPanel()
    {
        return subToolInfo;
    }
    
    public byte[] update()
    {
        shouldRedraw = false;
        if(tools.getRedraw())
        {
            subToolInfo.removeAll();
            subToolInfo.add(tools.getSelectPanel());
            shouldRedraw = true;
        }
        return tools.update(this);
    }
    
    public boolean isRedraw()
    {
        return shouldRedraw;
    }
    
    public void draw()
    {
        Graphics g = baseLayer.getGraphics();
        g.drawRect(0, 0, drawing.getWidth()-1, drawing.getHeight()-1);
        g = drawing.getGraphics();
        g.drawImage(baseLayer, 0, 0, null);
    }
    
    public int processIn(byte[] input, int i, int user)
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
    
    public Graphics getTempGraphics()
    {
        return tempLayer.getGraphics();
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
