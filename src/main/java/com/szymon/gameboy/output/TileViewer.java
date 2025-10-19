/**
 * @author 18bilkiewiczs
 * Outputs all of the tile data in memory
 */

package com.szymon.gameboy.output;

import javax.swing.*;

import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.ppu.utils.DisplayColour;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class TileViewer extends JPanel implements Runnable
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private Thread mainThread = new Thread(this);
	
	private static final long serialVersionUID = 5100989573626011395L;
	
	private Memory memory;
	
	private JFrame frame;
	private BufferedImage image;
	private int iScaleFactor;
	private int iWidth;
	private int iHeight;
	
	private final int iTILE_VIEWER_WIDTH = 16 * 8;
	private final int iTILE_VIEWER_HEIGHT = 24 * 8;
	
	private final int iVRAM_START_ADDRESS = 0x8000;
	
	private DisplayColour[][] tileData;
	
	private boolean bShown;

	// ---------------------------------------------
	// constructor
	// ---------------------------------------------
    public TileViewer(Memory mem, int sf) 
    {
    	memory = mem;
    	iScaleFactor = sf;
    	iWidth = iTILE_VIEWER_WIDTH * iScaleFactor;
    	iHeight = iTILE_VIEWER_HEIGHT * iScaleFactor;
    	
        frame = new JFrame("TileViewer");
        this.setPreferredSize(new Dimension(iWidth, iHeight));
        frame.setFocusable(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() 
        {
        	@Override
        	public void windowClosing(WindowEvent e)
        	{
        		setShown(false);
        	}
		});
        
        frame.getContentPane().add(this);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(false);
        
        tileData = new DisplayColour[iTILE_VIEWER_WIDTH][iTILE_VIEWER_HEIGHT];
        
        mainThread.start();
    }
    
    public void reset(Memory mem)
    {
    	memory = mem;
    }
    
    // ---------------------------------------------
 	// setters
 	// ---------------------------------------------
    public void setShown(boolean bShow)
    {
    	bShown = bShow;
    }
    
    public void setImage(BufferedImage image) 
    {
        this.image = image;
    }

    // ---------------------------------------------
 	// getters
 	// ---------------------------------------------
    public boolean getShown()
    {
    	return bShown;
    }

    // ---------------------------------------------
 	// methods
 	// ---------------------------------------------
    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
    
    //render method
    //renders the current frame in video memory
    public void render()
    {
    	BufferedImage image = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
    	
    	setTileData();
    	
    	//from the tileData array, set image RGB values
    	int iTileX = 0;
    	int iTileY = 0;
    	int iImX = 0;
    	for (int y = 0; y < iHeight; y++)
    	{
    		for (int x = 0; x < iWidth; x++)
    		{
    			iTileX = x / iScaleFactor;
    			iTileY = y / iScaleFactor;
    			
    			//need to reverse the x-direction
    			//iTileX = iWIDTH - iTileX - 1;
    			iImX = iWidth - x - 1;
    			
    			image.setRGB(iImX, y, tileData[iTileX][iTileY].getRGB());
    		}
    	}
    	
    	//render the new image
    	setImage(image);
    	repaint();
    }
    
    private void setTileData()
    {
    	Unsigned16 u16Addr = new Unsigned16(iVRAM_START_ADDRESS);
    	Unsigned8 u8Byte1 = new Unsigned8();
    	Unsigned8 u8Byte2 = new Unsigned8();
    	boolean bLSB = false;
    	boolean bMSB = false;
    	//384 tiles, 16 x 24
    	//each tile is 8 x 8
    	
    	//loop through all of the tiles
    	for (int yTile = 0; yTile < 24; yTile++)
    	{
    		for (int xTile = 15; xTile >= 0; xTile--)
    		{
    			//loop through all of the bytes for a tile, with 2 bytes per tile
    			for (int y = 0; y < 8; y++)
    			{
    				u8Byte1.setValue(memory.readMemory(u16Addr));
    				u16Addr.increment();
    				u8Byte2.setValue(memory.readMemory(u16Addr));
    				u16Addr.increment();
    				for (int x = 0; x < 8; x++)
    				{
    					bLSB = u8Byte1.getBit(x);
    					bMSB = u8Byte2.getBit(x);
    					
    					tileData[xTile*8 + x][yTile*8 + y] = getColour(bLSB, bMSB);
    				}
    			}
    		}
    	}
    }
    
    //gets the colour based off of two bits
    private DisplayColour getColour(boolean bLSB, boolean bMSB)
    {
    	if (bLSB && bMSB)
    	{
    		return DisplayColour.BLACK;
    	}
    	if (!bLSB && bMSB)
    	{
    		return DisplayColour.DARK;
    	}
    	if (bLSB && !bMSB)
    	{
    		return DisplayColour.LIGHT;
    	}
    	else
    	{
    		return DisplayColour.WHITE;
    	}
    }
    
    @Override
    public void run()
    {
    	while (true)
    	{
    		if (bShown)
    		{
    			frame.setVisible(true);
    			
    			while(bShown)
            	{
            		render();
            	}
    		}
    		else
    		{
    			frame.setVisible(false);
			}
    	}
    }
}

