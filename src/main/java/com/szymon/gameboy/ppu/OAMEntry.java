/**
 * @author 18bilkiewiczs
 * Class to represent an OAM (Object Attribute Memory) entry
 * Each OAM entry contains information about its corresponding object
 */

package com.szymon.gameboy.ppu;

import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class OAMEntry 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private Unsigned8 u8YPos;
	private Unsigned8 u8XPos;
	private Unsigned8 u8TileIndex;
	private Unsigned8 u8Attributes;
	//Attributes:
	//Bit 7: Priority: 0 = No, 1 = BG and Window colors 1–3 are drawn over this OBJ
	//Bit 6: Y flip: 0 = Normal, 1 = Entire OBJ is vertically mirrored
	//Bit 5: X flip: 0 = Normal, 1 = Entire OBJ is horizontally mirrored
	//Bit 4: DMG palette [Non CGB Mode only]: 0 = OBP0, 1 = OBP1
	//Bit 3: Bank [CGB Mode Only]: 0 = Fetch tile from VRAM bank 0, 1 = Fetch tile from VRAM bank 1
	//Bits 2-0: CGB palette [CGB Mode Only]: Which of OBP0–7 to use
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public OAMEntry()
	{
		reset();
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	public void setYPos(Unsigned8 yPos)
	{
		u8YPos.setValue(yPos);
	}
	
	public void setXPos(Unsigned8 xPos)
	{
		u8XPos.setValue(xPos);;
	}
	
	public void setTileIndex(Unsigned8 tileIndex)
	{
		u8TileIndex.setValue(tileIndex);
	}
	
	public void setAttributes(Unsigned8 attributes)
	{
		u8Attributes.setValue(attributes);
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public Unsigned8 getYPos()
	{
		return u8YPos;
	}
	
	public Unsigned8 getXPos()
	{
		return u8XPos;
	}
	
	public Unsigned8 getTileIndex()
	{
		return u8TileIndex;
	}
	
	public Unsigned8 getAttributes()
	{
		return u8Attributes;
	}
	
	//specific getters related to attributes
	public boolean getPriority()
	{
		return u8Attributes.getBit(7);
	}
	
	public boolean getYFlip()
	{
		return u8Attributes.getBit(6);
	}
	
	public boolean getXFlip()
	{
		return u8Attributes.getBit(5);
	}
	
	public boolean getDMGPalette()
	{
		return u8Attributes.getBit(4);
	}
	//other attributes are CGB only, so not necessary
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	public void reset()
	{
		u8YPos = new Unsigned8();
		u8XPos = new Unsigned8();
		u8TileIndex = new Unsigned8();
		u8Attributes = new Unsigned8();
	}
	
	public void getOAMEntry(Memory memory, int iIndex)
	{
		//read the bytes from OAM that refer to this object
		Unsigned16 u16Addr = new Unsigned16(0xFE00);
		u16Addr.add(iIndex * 4);
		
		setYPos(memory.readMemory(u16Addr));
		u16Addr.increment();
		setXPos(memory.readMemory(u16Addr));
		u16Addr.increment();
		setTileIndex(memory.readMemory(u16Addr));
		u16Addr.increment();
		setAttributes(memory.readMemory(u16Addr));
	}
}
