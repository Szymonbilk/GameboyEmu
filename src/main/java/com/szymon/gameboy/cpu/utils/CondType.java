/**
 * @author 18bilkiewiczs
 * Enum to represent all the condition types (e.g. NZ, Z etc.)
 */

package com.szymon.gameboy.cpu.utils;

public enum CondType
{
	NONE("NONE"),
	NZ("NZ"),
	Z("Z"),
	NC("NC"),
	C("C");
	
	private String szCond;
	
	private CondType(String value)
	{
		this.szCond = value;
	}
	
	public String getValue()
	{
		return this.szCond;
	}
}
