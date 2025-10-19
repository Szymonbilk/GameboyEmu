/**
 * @author 18bilkiewiczs
 * This class puts together the display related functionality
 * It acts as a state machine, transitioning between different states
 * and peforming the appropriate functions
 */

package com.szymon.gameboy.ppu;

import com.szymon.gameboy.cpu.CPUInterrupts;
import com.szymon.gameboy.cpu.utils.InterruptType;
import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.ppu.utils.DisplayColour;
import com.szymon.gameboy.ppu.utils.PPUMode;
import com.szymon.gameboy.ppu.utils.STATSrc;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class PPU 
{
    // ---------------------------------------------
 	// class variables
 	// ---------------------------------------------
	private final int iLINES_PER_FRAME = 154;
	private final int iTICKS_PER_LINE = 456;
	private final int iY_RES = 144;
	private final int iX_RES = 160;
	
	private int iLineTicks;
	public DisplayColour[][] videoBuffer;
	
	private LCD lcd;
	private LCDRegisters lcdRegisters;
	private Memory memory;
	private CPUInterrupts interrupts;
	
	private OAMEntry[] oamEntries;
	private int iNumEntries;
	
	private boolean bScanlineComplete;
	private boolean bOAMComplete;
	
	private int iWindowLine;
	
    // ---------------------------------------------
 	// constructors
 	// ---------------------------------------------
	public PPU(Memory mem, CPUInterrupts interruptsIn, LCD lcdIn)
	{
		initPPU(mem, interruptsIn, lcdIn);
	}
	
    // ---------------------------------------------
 	// methods
 	// ---------------------------------------------
	public void initPPU(Memory mem, CPUInterrupts interruptsIn, LCD lcdIn)
	{
		memory = mem;
		interrupts = interruptsIn;
		
		iLineTicks = 0;
		videoBuffer = new DisplayColour[iX_RES][iY_RES];
		
		lcd = lcdIn;
		lcdRegisters = lcd.getRegisters();
		
		lcdRegisters.setPPUMode(PPUMode.VBLANK);
		
		oamEntries = new OAMEntry[10];
		for (int i = 0; i < 10; i++)
		{
			oamEntries[i] = new OAMEntry();
		}
		iNumEntries = 0;
		
		bScanlineComplete = false;
		bOAMComplete = false;
		
		iWindowLine = 0;
	}
	
	//called every cycle
	public void tickPPU()
	{
		iLineTicks++;
		
		switch (lcdRegisters.getPPUMode())
		{
		case HBLANK:
			modeHBlank();
			break;
		case OAM:
			modeOAM();
			break;
		case TRANSFER:
			modeTransfer();
			break;
		case VBLANK:
			modeVBlank();
			break;
		}
	}
	
	//this incrementes LY, and ensures that LYC is set if LY = LYCompare, and calls an interrupt if requested
	private void incrementLY()
	{
		lcdRegisters.incrementLY();
		
		if (lcdRegisters.getLY().getValue() == lcdRegisters.getLYCompare().getValue())
		{
			lcdRegisters.setLYCFromLCDS(true);
			
			if (lcdRegisters.checkSTATInterrupt(STATSrc.LYC))
			{
				interrupts.requestCPUInterrupt(InterruptType.LCD_STAT);
			}
		}
		else
		{
			lcdRegisters.setLYCFromLCDS(false);
		}
	}
	
	private void resetLY()
	{
		lcdRegisters.resetLY();
	}
	
	//on the first run of OAM it loads in all of the objects for the current line
	private void modeOAM()
	{
		if (iLineTicks >= 80)
		{
			bOAMComplete = false;
			lcdRegisters.setPPUMode(PPUMode.TRANSFER);
		}
		
		if (!bOAMComplete)
		{
			bOAMComplete = true;
			loadOAM();
		}
	}
	
	//on the first run of transfer, the current scanline is rendered
	private void modeTransfer()
	{	
		if (iLineTicks >= 80 + 172)
		{
			bScanlineComplete = false;
			lcdRegisters.setPPUMode(PPUMode.HBLANK);
			
			if (lcdRegisters.checkSTATInterrupt(STATSrc.HBLANK))
			{
				interrupts.requestCPUInterrupt(InterruptType.LCD_STAT);
			}
		}
		
		//doing scanline rendering as more simple than full FIFO
		//render on the first line
		if (!bScanlineComplete)
		{
			bScanlineComplete = true;
			drawScanline();
		}
	}
	
	private void modeVBlank()
	{
		iWindowLine = 0;
		if (iLineTicks >= iTICKS_PER_LINE)
		{
			incrementLY();
			
			if (lcdRegisters.getLY().getValue() >= iLINES_PER_FRAME)
			{
				lcdRegisters.setPPUMode(PPUMode.OAM);
				resetLY();
			}
			
			iLineTicks = 0;
		}
	}
	
	private void modeHBlank()
	{
		if (iLineTicks >= iTICKS_PER_LINE)
		{
			incrementLY();
			
			if (lcdRegisters.getLY().getValue() >= iY_RES)
			{
				lcdRegisters.setPPUMode(PPUMode.VBLANK);
				interrupts.requestCPUInterrupt(InterruptType.VBLANK);
				
				if (lcdRegisters.checkSTATInterrupt(STATSrc.VBLANK))
				{
					interrupts.requestCPUInterrupt(InterruptType.LCD_STAT);
				}
				
				//save the cart if needed
				if (memory.getCartNeedSave())
				{
					memory.saveCartBattery();
				}
			}
			else
			{
				lcdRegisters.setPPUMode(PPUMode.OAM);
			}
			
			iLineTicks = 0;
		}
	}
	
	//handles the drawing to the screen
	private void drawScanline()
	{
		if (lcdRegisters.getBGWindowEnable())
		{
			renderTiles();
		}
		
		if (lcdRegisters.getObjEnable())
		{
			renderSprites();
		}
	}
	
	//renders background/window tiles to the screen
	private void renderTiles()
	{
		Unsigned16 u16TileDataAddr = new Unsigned16();
		Unsigned16 u16TileMapAddr = new Unsigned16();
		Unsigned16 u16General = new Unsigned16();
		
		Unsigned8 u8Data1 = new Unsigned8();
		Unsigned8 u8Data2 = new Unsigned8();
		
		Unsigned8 u8ScrollY = new Unsigned8(lcdRegisters.getScrollY());
		Unsigned8 u8ScrollX = new Unsigned8(lcdRegisters.getScrollX());
		Unsigned8 u8WindowY = new Unsigned8(lcdRegisters.getWinY());
		Unsigned8 u8WindowX = new Unsigned8(lcdRegisters.getWinX());
		u8WindowX.sub(7);
		Unsigned8 u8LY = new Unsigned8(lcdRegisters.getLY());
		
		boolean bSigned = false;
		boolean bUsedWindow = false;
		
		Unsigned8 u8YPos = new Unsigned8();
		Unsigned8 u8XPos = new Unsigned8();
		int iTileRow = 0;
		
		int iTileCol = 0;
		int iTileNum = 0;
		int iLine = 0;
		int iColourBit = 0;
		
		boolean bMSB = false;
		boolean bLSB = false;
		DisplayColour colour = DisplayColour.WHITE;
		
		u16TileDataAddr.setValue(lcdRegisters.getBGWindowTileDataStart());
		if (u16TileDataAddr.getValue() == 0x8800)
		{
			bSigned = true;
		}
		
		for (int iPixel = 0; iPixel < 160; iPixel++)
		{
			//check whether to use the window or background
			if (u8WindowY.getValue() <= u8LY.getValue() && iPixel >= u8WindowX.getValue() && lcdRegisters.getWindowEnable())
			{
				bUsedWindow = true;
				u8YPos.setValue(iWindowLine);
				u8XPos.setValue(iPixel);
				u8XPos.sub(u8WindowX);
				u16TileMapAddr.setValue(lcdRegisters.getWindowTileMapStart());
			}
			else
			{
				u8YPos.setValue(u8LY);
				u8YPos.add(u8ScrollY);
				u8XPos.setValue(iPixel);
				u8XPos.add(u8ScrollX);
				u16TileMapAddr.setValue(lcdRegisters.getBGTileMapStart());
			}
			
			iTileRow = (u8YPos.getValue() / 8) * 32;
			iTileCol = u8XPos.getValue() / 8;
			
			//get tile identity num
			u16General.setValue(u16TileMapAddr);
			u16General.add(iTileRow + iTileCol);
			if (bSigned)
			{
				iTileNum = memory.readMemory(u16General).getSignedValue();
				iTileNum += 128;
			}
			else
			{
				iTileNum = memory.readMemory(u16General).getValue();
			}
			
			u16General.setValue(u16TileDataAddr);
			u16General.add(iTileNum * 16);
			
			iLine = u8YPos.getValue() % 8;
			iLine *= 2;
			
			u16General.add(iLine);
			u8Data1.setValue(memory.readMemory(u16General));
			u16General.increment();
			u8Data2.setValue(memory.readMemory(u16General));
			
			//pixel 0 in the tile is bit 7 of data 1 and data 2
			iColourBit = u8XPos.getValue() % 8;
			iColourBit = 7 - iColourBit;
			
			//get the colour data
			bLSB = u8Data1.getBit(iColourBit);
			bMSB = u8Data2.getBit(iColourBit);
			colour = lcd.getBGColour(convertTwoBooleanToInt(bMSB, bLSB));
			
			videoBuffer[iPixel][u8LY.getValue()] = colour;
		}
		
		if (bUsedWindow)
		{
			iWindowLine++;
		}
	}
	
	//renders objects to the screen
	private void renderSprites()
	{
		Unsigned16 u16Address = new Unsigned16();
		
		Unsigned8 u8Data1 = new Unsigned8();
		Unsigned8 u8Data2 = new Unsigned8();
		
		int iYPos = 0;
		int iXPos = 0;
		int iTileIndex = 0;
		
		int iLY = lcdRegisters.getLY().getValue();
		
		int iYSize = lcdRegisters.getObjHeight();
		
		int iLine = 0;
		int iColourBit = 0;
		int iPixelX = 0;
		
		boolean bLSB = false;
		boolean bMSB = false;
		
		DisplayColour colour = DisplayColour.WHITE;
		
		for (int i = 0; i < iNumEntries; i++)
		{
			iYPos = oamEntries[i].getYPos().getValue() - 16;
			iXPos = oamEntries[i].getXPos().getValue() - 8;
			iTileIndex = oamEntries[i].getTileIndex().getValue();
			
			iLine = iLY - iYPos;
			
			//in 8x16 mode, ignore bit 0 of tile index
			if (iYSize == 16)
			{
				iTileIndex &= ~0b1;
			}
			
			if (oamEntries[i].getYFlip())
			{
				iLine = iYSize - iLine - 1;
			}
			iLine *= 2;
			
			u16Address.setValue(0x8000);
			u16Address.add(iTileIndex * 16);
			u16Address.add(iLine);
			
			u8Data1.setValue(memory.readMemory(u16Address));
			u16Address.increment();
			u8Data2.setValue(memory.readMemory(u16Address));
			u16Address.increment();
			
			//read data in backwards
			for (int iTilePixel = 7; iTilePixel >= 0; iTilePixel--)
			{
				iColourBit = iTilePixel;
				if (oamEntries[i].getXFlip())
				{
					iColourBit = 7 - iTilePixel;
				}
				
				bLSB = u8Data1.getBit(iColourBit);
				bMSB = u8Data2.getBit(iColourBit);
				
				//colour index 0 is ignored
				if (!bMSB && !bLSB)
				{
					continue;
				}
				
				if (oamEntries[i].getDMGPalette())
				{
					colour = lcd.getSP2Colour(convertTwoBooleanToInt(bMSB, bLSB));
				}
				else
				{
					colour = lcd.getSP1Colour(convertTwoBooleanToInt(bMSB, bLSB));
				}
				
				iPixelX = 0 - iTilePixel;
				iPixelX += 7;
				iPixelX += iXPos;
				
				//make sure on screen!
				if (iPixelX < 0 || iPixelX > 159)
				{
					continue;
				}

				//check if pixel is hidden behind background
				if (oamEntries[i].getPriority())
				{
					if (videoBuffer[iPixelX][iLY] != DisplayColour.WHITE)
					{
						continue;
					}
				}
				
				videoBuffer[iPixelX][iLY] = colour;
			}
		}
	}
	
	//load the up to 10 sprites from OAM for the required scanline
	private void loadOAM()
	{
		OAMEntry[] oamTemp = new OAMEntry[10];
		int iLY = lcdRegisters.getLY().getValue();
		int iHeight = lcdRegisters.getObjHeight();
		iNumEntries = 0;
		
		int iYPos = 0;
		
		OAMEntry entry = new OAMEntry();
		
		//loop through the 40 tiles
		for (int i = 0; i < 40; i++)
		{
			entry.getOAMEntry(memory, i);
			iYPos = entry.getYPos().getValue() - 16;
			//select only if the object would be on the current scanline
			if ((iLY >= iYPos) && (iLY < (iYPos + iHeight)))
			{
				oamTemp[iNumEntries] = new OAMEntry();
				oamTemp[iNumEntries].getOAMEntry(memory, i);
				//oamEntries[iNumEntries].getOAMEntry(memory, i);
				iNumEntries++;
			}
			
			//finish once 10 have been found
			if (iNumEntries >= 10)
			{
				break;
			}
		}
		
		//now sort the list, back to front, so that the priority objects are drawn last
		//this ensures that they appear on top
		//also, consider x-coordinates
		//if two objects overlap, the one with the smaller x-coordinate takes priority
		int iReverseIndex = iNumEntries - 1;
		for (int i = 0; i < iNumEntries; i++)
		{
			oamEntries[i] = oamTemp[iReverseIndex];
			iReverseIndex--;
		}
		
		int iXPos1 = 0;
		int iXPos2 = 0;
		int j = 0;
		for (int i = 1; i < iNumEntries; i++)
		{
			entry = oamEntries[i];
			j = i - 1;
			
			iXPos1 = oamEntries[j].getXPos().getValue();
			iXPos2 = oamEntries[i].getXPos().getValue();
			while (j >= 0 && iXPos2 > iXPos1 && iXPos2 <= (iXPos1 + 8))
			{
				iXPos1 = oamEntries[j].getXPos().getValue();
				iXPos2 = oamEntries[i].getXPos().getValue();
				oamEntries[j + 1] = oamEntries[j];
				j--;
			}
			oamEntries[j + 1] = entry;
		}
	}
	
	private int convertTwoBooleanToInt(boolean bMSB, boolean bLSB)
	{
		if (!bMSB && !bLSB)
		{
			return 0;
		}
		else if (!bMSB && bLSB)
		{
			return 1;
		}
		else if (bMSB && !bLSB)
		{
			return 2;
		}
		else
		{
			return 3;
		}
	}
}
