/**
 * @author 18bilkiewiczs
 * Class that contains the values of whether certain buttons are pressed or not
 * Also converts this information as appropriate when being accessed via memory
 * Note: This register is set to 0xFF typically
 * When buttons are pressed, the relevant bit is cleared
 */

package com.szymon.gameboy.joypad;

import com.szymon.gameboy.utils.BitOps;
import com.szymon.gameboy.utils.Unsigned8;

public class Joypad 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	//these two booleans are set by the game, deciding whether to read the values of buttons or direction
	private boolean bButtonSelected;	//indicates that buttons are selected (A, B, Start, Select)
	private boolean bDirectionSelected;	//indicates that the directions is selected
	//booleans for each of the separate buttons on the gameboy
	private boolean bStart;
	private boolean bSelect;
	private boolean bA;
	private boolean bB;
	private boolean bLeft;
	private boolean bRight;
	private boolean bUp;
	private boolean bDown;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public Joypad()
	{
		bButtonSelected = false;
		bDirectionSelected = false;
		bStart = false;
		bSelect = false;
		bA = false;
		bB = false;
		bLeft = false;
		bRight = false;
		bUp = false;
		bDown = false;
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	//used from memory
	public void setJoypad(Unsigned8 u8Value)
	{
		bButtonSelected = BitOps.convertIntToBool(u8Value.getValue() & 0x20);
		bDirectionSelected = BitOps.convertIntToBool(u8Value.getValue() & 0x10);
	}
	
	public void setStart(boolean start)
	{
		bStart = start;
	}
	
	public void setSelect(boolean select)
	{
		bSelect = select;
	}
	
	public void setA(boolean a)
	{
		bA = a;
	}
	
	public void setB(boolean b)
	{
		bB = b;
	}
	
	public void setLeft(boolean left)
	{
		bLeft = left;
	}
	
	public void setRight(boolean right)
	{
		bRight = right;
	}
	
	public void setUp(boolean up)
	{
		bUp = up;
	}
	
	public void setDown(boolean down)
	{
		bDown = down;
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	//used from memory, only clearing bits if the button is pressed, 
	//and if it has been selected to check if those buttons are being pressed
	public Unsigned8 getJoypad()
	{
		Unsigned8 u8Output = new Unsigned8(0xFF);
		
		if (!bButtonSelected)
		{
			u8Output.clearBit(5);
			if (bStart)
			{
				u8Output.clearBit(3);
			}
			else if (bSelect)
			{
				u8Output.clearBit(2);
			}
			else if (bA)
			{
				u8Output.clearBit(0);
			}
			else if (bB)
			{
				u8Output.clearBit(1);
			}
		}
		
		if (!bDirectionSelected)
		{
			u8Output.clearBit(4);
			if (bLeft)
			{
				u8Output.clearBit(1);
			}
			else if (bRight)
			{
				u8Output.clearBit(0);
			}
			else if (bUp)
			{
				u8Output.clearBit(2);
			}
			else if (bDown)
			{
				u8Output.clearBit(3);
			}
		}
		
		return u8Output;
	}
}
