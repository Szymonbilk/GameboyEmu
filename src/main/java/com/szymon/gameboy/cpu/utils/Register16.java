/**
 * @author 18bilkiewiczs
 * Class to represent a CPU 16 bit register
 */

package com.szymon.gameboy.cpu.utils;

import com.szymon.gameboy.utils.Unsigned16;

public class Register16 extends Unsigned16
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private RegType regType;	//to store the type of register
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	//must pass a register type when creating the register
	public Register16(RegType rType)
	{
		regType = rType;
	}
	//once initialised, the register type cannot be changed
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public RegType getRegType()
	{
		return regType;
	}
}