/**
 * @author 18bilkiewiczs
 * Enum to represent all the CPU registers, in both the separate 8 bit registers, and as combined 16 bit registers
 */

package com.szymon.gameboy.cpu.utils;

public enum RegType
{
	NONE("NONE"),
	A("A"),
	F("F"),
	B("B"),
	C("C"),
	D("D"),
	E("E"),
	H("H"),
	L("L"),
	AF("AF"),
	BC("BC"),
	DE("DE"),
	HL("HL"),
	SP("SP"),
	PC("PC");
	
	private String szReg;
	
	private RegType(String value)
	{
		this.szReg = value;
	}
	
	public String getValue()
	{
		return this.szReg;
	}
}
