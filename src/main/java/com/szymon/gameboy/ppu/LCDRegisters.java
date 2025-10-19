/**
 * @author 18bilkiewiczs
 * Class that stores all of the LCD registers
 */

package com.szymon.gameboy.ppu;

import com.szymon.gameboy.ppu.utils.PPUMode;
import com.szymon.gameboy.ppu.utils.STATSrc;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class LCDRegisters 
{
    // ---------------------------------------------
 	// class variables
 	// ---------------------------------------------
	private Unsigned8 u8LCDC;
	private Unsigned8 u8LCDS;
	private Unsigned8 u8ScrollY;
	private Unsigned8 u8ScrollX;
	private Unsigned8 u8LY;
	private Unsigned8 u8LYCompare;
	private Unsigned8 u8DMA;
	private Unsigned8 u8BGPalette;
	private Unsigned8 u8OBJPalette1;
	private Unsigned8 u8OBJPalette2;
	private Unsigned8 u8WinY;
	private Unsigned8 u8WinX;
	
    // ---------------------------------------------
 	// constructors
 	// ---------------------------------------------
	public LCDRegisters()
	{
		u8LCDC = new Unsigned8(0x91);
		u8LCDS = new Unsigned8(0x81);
		u8ScrollY = new Unsigned8();
		u8ScrollX = new Unsigned8();
		u8LY = new Unsigned8(0x91);
		u8LYCompare = new Unsigned8();
		u8DMA = new Unsigned8(0xFF);
		u8BGPalette = new Unsigned8(0xFC);
		u8OBJPalette1 = new Unsigned8(0xFF);
		u8OBJPalette2 = new Unsigned8(0xFF);
		u8WinY = new Unsigned8();
		u8WinX = new Unsigned8();
	}
	
    // ---------------------------------------------
 	// setters
 	// ---------------------------------------------
	public void writeMemory(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		switch (u16Address.getValue())
		{
		case 0xFF40:
			u8LCDC.setValue(u8Data);
			break;
		case 0xFF41:
			//bits 0, 1, 2 are read only
			Unsigned8 u8Masked = new Unsigned8();
			u8Masked.setValue(u8Data.getValue() & 0b11111000);
			u8LCDS.setValue(u8Masked);
			break;
		case 0xFF42:
			u8ScrollY.setValue(u8Data);
			break;
		case 0xFF43:
			u8ScrollX.setValue(u8Data);
			break;
		case 0xFF44:
			//READ-ONLY
			break;
		case 0xFF45:
			u8LYCompare.setValue(u8Data);
			break;
		case 0xFF46:
			u8DMA.setValue(u8Data);
			break;
		case 0xFF47:
			u8BGPalette.setValue(u8Data);
			break;
		case 0xFF48:
			u8OBJPalette1.setValue(u8Data);
			break;
		case 0xFF49:
			u8OBJPalette2.setValue(u8Data);
			break;
		case 0xFF4A:
			u8WinY.setValue(u8Data);
			break;
		case 0xFF4B:
			u8WinX.setValue(u8Data);
			break;
		}
	}
	
    // ---------------------------------------------
 	// getters
 	// ---------------------------------------------	
	public Unsigned8 readMemory(Unsigned16 u16Address)
	{
		switch (u16Address.getValue())
		{
		case 0xFF40:
			return u8LCDC;
		case 0xFF41:
			return u8LCDS;
		case 0xFF42:
			return u8ScrollY;
		case 0xFF43:
			return u8ScrollX;
		case 0xFF44:
			return u8LY;
		case 0xFF45:
			return u8LYCompare;
		case 0xFF46:
			return u8DMA;
		case 0xFF47:
			return u8BGPalette;
		case 0xFF48:
			return u8OBJPalette1;
		case 0xFF49:
			return u8OBJPalette2;
		case 0xFF4A:
			return u8WinY;
		case 0xFF4B:
			return u8WinX;
		}
		
		return new Unsigned8();
	}
	
	public Unsigned8 getLY()
	{
		return u8LY;
	}
	
	public Unsigned8 getScrollY()
	{
		return u8ScrollY;
	}
	
	public Unsigned8 getScrollX()
	{
		return u8ScrollX;
	}
	
	public Unsigned8 getWinY()
	{
		return u8WinY;
	}
	
	public Unsigned8 getWinX()
	{
		return u8WinX;
	}
	
	public Unsigned8 getLYCompare()
	{
		return u8LYCompare;
	}
	
    // ---------------------------------------------
 	// methods
 	// ---------------------------------------------
	public void incrementLY()
	{
		u8LY.increment();
	}
	
	public void resetLY()
	{
		u8LY.setValue(0);
	}
	
	//LCDC
	//7-LCD & PPU enable: 0 = Off; 1 = On
	//6-Window tile map area: 0 = 9800–9BFF; 1 = 9C00–9FFF
	//5-Window enable: 0 = Off; 1 = On
	//4-BG & Window tile data area: 0 = 8800–97FF; 1 = 8000–8FFF
	//3-BG tile map area: 0 = 9800–9BFF; 1 = 9C00–9FFF
	//2-OBJ size: 0 = 8×8; 1 = 8×16
	//1-OBJ enable: 0 = Off; 1 = On
	//0-BG & Window enable / priority [Different meaning in CGB Mode]: 0 = Off; 1 = On
	public boolean getLCDEnable()
	{
		return u8LCDC.getBit(7);
	}
	
	public Unsigned16 getWindowTileMapStart()
	{
		Unsigned16 u16Address = new Unsigned16();
		
		if (u8LCDC.getBit(6))
		{
			u16Address.setValue(0x9C00);
		}
		else
		{
			u16Address.setValue(0x9800);
		}
		
		return u16Address;
	}
	
	public boolean getWindowEnable()
	{
		return u8LCDC.getBit(5);
	}
	
	public Unsigned16 getBGWindowTileDataStart()
	{
		Unsigned16 u16Address = new Unsigned16();
		
		if (u8LCDC.getBit(4))
		{
			u16Address.setValue(0x8000);
		}
		else
		{
			u16Address.setValue(0x8800);
		}
		
		return u16Address;
	}
	
	public Unsigned16 getBGTileMapStart()
	{
		Unsigned16 u16Address = new Unsigned16();
		
		if (u8LCDC.getBit(3))
		{
			u16Address.setValue(0x9C00);
		}
		else
		{
			u16Address.setValue(0x9800);
		}
		
		return u16Address;
	}
	
	public int getObjHeight()
	{
		int iHeight = 8;
		
		if (u8LCDC.getBit(2))
		{
			iHeight = 16;
		}
		
		return iHeight;
	}
	
	public boolean getObjEnable()
	{
		return u8LCDC.getBit(1);
	}
	
	public boolean getBGWindowEnable()
	{
		return u8LCDC.getBit(0);
	}
	
	//LCDS
	//6-LYC int select (Read/Write): If set, selects the LYC == LY condition for the STAT interrupt.
	//5-Mode 2 int select (Read/Write): If set, selects the Mode 2 condition for the STAT interrupt.
	//4-Mode 1 int select (Read/Write): If set, selects the Mode 1 condition for the STAT interrupt.
	//3-Mode 0 int select (Read/Write): If set, selects the Mode 0 condition for the STAT interrupt.
	//2-LYC == LY (Read-only): Set when LY contains the same value as LYC; it is constantly updated.
	//1-0-PPU mode (Read-only): Indicates the PPU’s current status.
	//Mode 2 - OAM scan
	//Mode 1 - VBlank
	//Mode 0 - HBlank
	
	public boolean checkSTATInterrupt(STATSrc src)
	{
		if (u8LCDS.getBit(6) && src == STATSrc.LYC)
		{
			return true;
		}
		
		if (u8LCDS.getBit(5) && src == STATSrc.OAM)
		{
			return true;
		}
		
		if (u8LCDS.getBit(4) && src == STATSrc.VBLANK)
		{
			return true;
		}
		
		if (u8LCDS.getBit(3) && src == STATSrc.HBLANK)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean getLYCFromLCDS()
	{
		return u8LCDS.getBit(2);
	}
	
	protected void setLYCFromLCDS(boolean bBit)
	{
		if (bBit)
		{
			u8LCDS.setBit(2);
		}
		else 
		{
			u8LCDS.clearBit(2);
		}
	}
	
	public PPUMode getPPUMode()
	{
		int iMode = u8LCDS.getValue() & 0b11;
		PPUMode mode = PPUMode.VBLANK;
		
		switch (iMode)
		{
		case 0b00:
			mode = PPUMode.HBLANK;
			break;
		case 0b01:
			mode = PPUMode.VBLANK;
			break;
		case 0b10:
			mode = PPUMode.OAM;
			break;
		case 0b11:
			mode = PPUMode.TRANSFER;
			break;
		}
		
		return mode;
	}
	
	protected void setPPUMode(PPUMode mode)
	{
		switch (mode)
		{
		case HBLANK:
			u8LCDS.clearBit(0);
			u8LCDS.clearBit(1);
			break;
		case VBLANK:
			u8LCDS.setBit(0);
			u8LCDS.clearBit(1);
			break;
		case OAM:
			u8LCDS.clearBit(0);
			u8LCDS.setBit(1);
			break;
		case TRANSFER:
			u8LCDS.setBit(0);
			u8LCDS.setBit(1);
			break;
		}
	}
}
