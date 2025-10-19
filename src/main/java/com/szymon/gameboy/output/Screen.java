/**
 * @author 18bilkiewiczs
 * Class to output the screen of the Game Boy
 */

package com.szymon.gameboy.output;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.szymon.gameboy.Emu;
import com.szymon.gameboy.joypad.Joypad;
import com.szymon.gameboy.ppu.PPU;
import com.szymon.gameboy.ppu.utils.DisplayColour;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class Screen extends JPanel implements Runnable
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private static final long serialVersionUID = 7544296288989856095L;

	private Thread mainThread = new Thread(this);	//runs in a separate thread to the main code
	
	//emu so that it can update the game that is selected
	private Emu emu;
	//this class needs the video buffer from the PPU, and needs the joypad 
	//as this is where it is updated from the keyboard
	private PPU ppu;
	private Joypad joypad;
	private TileViewer tileViewer;
	
	//GUI output
	private JFrame frame;
	
	private JPopupMenu popupMenu;
	private JMenuItem fileItem;
	private JMenuItem toggleTileViewer;
	private JMenuItem changeDisplayColour;
	private JFileChooser fileChooser;
	private int iFileResult;
	
	private BufferedImage image;
	private int iScaleFactor;
	private int iWidth;
	private int iHeight;
	//screen width and height of the actual gameboy screen
	private final int iSCREEN_WIDTH = 160;
	private final int iSCREEN_HEIGHT = 144;
	//joypad input
	private boolean bStart;
	private boolean bSelect;
	private boolean bA;
	private boolean bB;
	private boolean bLeft;
	private boolean bRight;
	private boolean bUp;
	private boolean bDown;
	//stores the data to be outputted to the screen
	private DisplayColour[][] videoData;
	
	private boolean bShown;

	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
    public Screen(Emu emuIn, PPU ppuIn, Joypad joypadIn, TileViewer tileViewerIn, int iSF) 
    {
    	iScaleFactor = iSF;
    	setupScreen();
    	reset(emuIn, ppuIn, joypadIn, tileViewerIn);
    	mainThread.start();
    }
    
    //sets up the main GUI elements
    public void setupScreen()
    {	
    	bShown = true;
    	
    	iWidth = iSCREEN_WIDTH * iScaleFactor;
    	iHeight = iSCREEN_HEIGHT * iScaleFactor;
    	
    	videoData = new DisplayColour[iSCREEN_WIDTH][iSCREEN_HEIGHT];
    	
    	frame = new JFrame("Screen");
        this.setPreferredSize(new Dimension(iWidth, iHeight));
        frame.setFocusable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
         
        //set up popup menu
        fileChooser = new JFileChooser("rsc/roms/");
        popupMenu = new JPopupMenu();
        fileItem = new JMenuItem("Choose ROM");
        fileItem.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
 			{
        		iFileResult = fileChooser.showOpenDialog(frame);
 				
 				if (iFileResult == JFileChooser.APPROVE_OPTION)
 		    	{
 		    		emu.reset(fileChooser.getSelectedFile(), iScaleFactor);
 		    		tileViewer.reset(emu.getMemory());
 		    	}
 			}
 		});
         
        toggleTileViewer = new JMenuItem("Toggle Tile Viewer");
        toggleTileViewer.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
 			{
        		if (tileViewer != null)
        		{
        			tileViewer.setShown(!tileViewer.getShown());
        		}
 			}
 		});
         
        changeDisplayColour = new JMenuItem("Change Display Colours");
        changeDisplayColour.addActionListener(new ActionListener() 
        {
 			public void actionPerformed(ActionEvent e) 
 			{
 				String szWhite = JOptionPane.showInputDialog("Enter White Hex Color: ");
 				DisplayColour.WHITE.setRGB(Integer.parseInt(szWhite, 16));
 				String szLight = JOptionPane.showInputDialog("Enter Light Hex Color: ");
 				DisplayColour.LIGHT.setRGB(Integer.parseInt(szLight, 16));
 				String szDark = JOptionPane.showInputDialog("Enter Dark Hex Color: ");
 				DisplayColour.DARK.setRGB(Integer.parseInt(szDark, 16));
 				String szBlack = JOptionPane.showInputDialog("Enter Black Hex Color: ");
 				DisplayColour.BLACK.setRGB(Integer.parseInt(szBlack, 16));
 			}
 		});
         
        popupMenu.add(fileItem);
        popupMenu.add(toggleTileViewer);
        popupMenu.add(changeDisplayColour);
        frame.addMouseListener(new MouseListener() 
        {
 			@Override
 			public void mouseReleased(MouseEvent e) {}
 			
 			@Override
 			public void mousePressed(MouseEvent e) 
 			{
 				if (SwingUtilities.isRightMouseButton(e))
 				{
 					popupMenu.show(frame, e.getX(), e.getY());
 				}
 			}
 			
 			@Override
 			public void mouseExited(MouseEvent e) {}
 			
 			@Override
 			public void mouseEntered(MouseEvent e) {}
 			
 			@Override
 			public void mouseClicked(MouseEvent e) {}
 		});
         
        popupMenu.addPopupMenuListener(new PopupMenuListener() 
        {
 			@Override
 			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
 			{
 				emu.setPaused(true);
 			}
 			
 			@Override
 			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) 
 			{
 				emu.setPaused(false);
 			}
 			
 			@Override
 			public void popupMenuCanceled(PopupMenuEvent e) {}
 		});
         
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    //sets the values for the class variables
    public void reset(Emu emuIn, PPU ppuIn, Joypad joypadIn, TileViewer tileViewerIn)
    {
    	emu = emuIn;
    	ppu = ppuIn;
    	joypad = joypadIn;
    	
    	tileViewer = tileViewerIn;
    	
    	bStart = false;
    	bSelect = false;
    	bA = false;
    	bB = false;
    	bLeft = false;
    	bRight = false;
    	bUp = false;
    	bDown = false;
        
        //dealing with keyboard input
        //TODO - allow keybinds to be changed
        frame.addKeyListener(new KeyListener() 
        {
			@Override
			public void keyTyped(KeyEvent e) 
			{
			}
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				if (e.getKeyCode() == KeyEvent.VK_W)
				{
					bUp = false;
				}
				else if (e.getKeyCode() == KeyEvent.VK_A)
				{
					bLeft = false;
				}
				else if (e.getKeyCode() == KeyEvent.VK_S)
				{
					bDown = false;
				}
				else if (e.getKeyCode() == KeyEvent.VK_D)
				{
					bRight = false;
				}
				else if (e.getKeyCode() == KeyEvent.VK_O)
				{
					bB = false;
				}
				else if (e.getKeyCode() == KeyEvent.VK_P)
				{
					bA = false;
				}
				else if (e.getKeyCode() == KeyEvent.VK_K)
				{
					bSelect = false;
				}
				else if (e.getKeyCode() == KeyEvent.VK_L)
				{
					bStart = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if (e.getKeyCode() == KeyEvent.VK_W)
				{
					bUp = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_A)
				{
					bLeft = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_S)
				{
					bDown = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_D)
				{
					bRight = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_O)
				{
					bB = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_P)
				{
					bA = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_K)
				{
					bSelect = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_L)
				{
					bStart = true;
				}
			}
		});
    	
        if (fileChooser.getSelectedFile() != null)
        {
        	setName(fileChooser.getSelectedFile().getName());
        }
    }
    
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
    //set whether it should be shown
    public void setShown(boolean bShow)
    {
    	bShown = bShow;
    }
    
    //set the name of the window
    public void setName(String szName)
    {
    	frame.setTitle(szName);
    }

    //sets the image to be outputted
    public void setImage(BufferedImage image) 
    {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
    
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
    //gets the data to be outputted to the screen
    public void getVideoData()
    {
    	for (int x = 0; x < iSCREEN_WIDTH; x++)
    	{
    		for (int y = 0; y < iSCREEN_HEIGHT; y++)
    		{
    			if (ppu.videoBuffer[x][y] != null)
    			{
    				videoData[x][y] = ppu.videoBuffer[x][y];
    			}
    			else 
    			{
					videoData[x][y] = DisplayColour.WHITE;
				}
    		}
    	}
    }
    
    //render method
    //renders the current frame in video memory
    public void render()
    {
    	BufferedImage image = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
    	
    	getVideoData();
    	
    	int iTileX = 0;
    	int iTileY = 0;
    	for (int y = 0; y < iHeight; y++)
    	{
    		for (int x = 0; x < iWidth; x++)
    		{
    			iTileX = x / iScaleFactor;
    			iTileY = y / iScaleFactor;
    			
    			image.setRGB(x, y, videoData[iTileX][iTileY].getRGB());
    		}
    	}
    	
    	//render the new image
    	setImage(image);
    	repaint();
    }
    
    //updates the joypad
    public void updateJoypad()
    {
    	joypad.setA(bA);
    	joypad.setB(bB);
    	joypad.setDown(bDown);
    	joypad.setLeft(bLeft);
    	joypad.setRight(bRight);
    	joypad.setSelect(bSelect);
    	joypad.setStart(bStart);
    	joypad.setUp(bUp);
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

