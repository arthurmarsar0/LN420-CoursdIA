package projet1.gomoku.controllers.ai;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import projet1.gomoku.controllers.PlayerController;
import projet1.gomoku.gamecore.Coords;
import projet1.gomoku.gamecore.GomokuBoard;
import projet1.gomoku.gamecore.enums.Player;

/**
 * Représente un joueur de Gomoku RESEAU  
 */
public class NetWorkPlayer extends PlayerController 
{
    private String ipServer;

	public NetWorkPlayer(String ipServer)
    {
		this.ipServer = ipServer;
    }

    @Override
    public Coords play(GomokuBoard board, Player player)
    {
    	System.out.println("En attente de la réponse du serveur .... ");
    	try
		{
			String str = board.asString();
			
			Socket sk = new Socket(ipServer, 7200);
			
			OutputStream os = sk.getOutputStream();
			os.write(player==Player.Black ? 'W' : 'B');  // L'inversion est normale : on dit que les blancs ont joué ce coup là , et c'est les noirs qui le constatent
			os.write(str.getBytes());
			
			InputStream is = sk.getInputStream();

			String resp = "";
			byte[] buf = new byte[1024];
			int len = is.read(buf);
			while(len!=-1)
			{
				resp = resp+new String(buf,0,len);
				len = is.read(buf);
			}
			
			sk.close();
			
			String[] xy = resp.split(":");
			
			Coords coords = new Coords();
			coords.row = Integer.parseInt(xy[0]);
			coords.column = Integer.parseInt(xy[1]);
			
			System.out.println("Le serveur indique que le joueur distant a joué "+(coords.row)+"/"+(coords.column));
			
			return coords;
		} 
    	catch (NumberFormatException | IOException e)
		{
			throw new RuntimeException(e);
		}
    }
}
