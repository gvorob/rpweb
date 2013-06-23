/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

/**
 *
 * @author George
 */
public class Reminder {
    public int[] remindParams;
    public int delay;
    public Tool sender;
    
    public Reminder(Tool t,int d,int[] i)
    {
        sender = t;
        delay = d;
        remindParams = i;
    }
}
