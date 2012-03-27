/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author George
 */
public class RPWeb{

    
    public static void main(String[] args){
        RPWeb derp = new RPWeb();
        derp.start();
    }
    
    public void start()
    {
        Launcher l = new Launcher();
        Socket sock = l.launch();
        if(l.isHost)
        {
            l.close();
            System.out.println("Starting server...");
            RPServer rpserv = new RPServer();
            rpserv.initHosting(beginHosting(l.hostInfo(),rpserv));
        }
        else
        {
            l.close();
            RPClient rpclient = new RPClient();
            rpclient.startClient(sock);
        }
        System.exit(0);
    }
    
    private class Hoster implements Runnable
    {
        public boolean hosting = true;
        public ServerSocket sock;
        public RPServer main;
        
        public Hoster(ServerSocket s, RPServer r)
        {
            sock = s;
            main = r;
        }
        
        public void run()
        {
            while(true)
            {
                try 
                {
                    Socket temp = sock.accept();
                    main.addConnection(temp);
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(RPWeb.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public Thread beginHosting(ServerSocket servSock, RPServer serv)
    {
        Thread t = new Thread(new Hoster(servSock, serv));
        t.start();
        return t;
    }
}
