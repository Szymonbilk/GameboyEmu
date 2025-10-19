/**
 * @author 18bilkiewiczs
 * Class to represent a CPU 8 bit register
 */

package com.szymon.gameboy.cpu.utils;

import com.szymon.gameboy.utils.Unsigned8;

public class Register8 extends Unsigned8
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private RegType regType;	//to store the type of register
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	//must pass a register type when creating the register
	public Register8(RegType rType)
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
