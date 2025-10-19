/**
 * @author 18bilkiewiczs
 * Enum to represent all the instruction types (e.g. LD, ADD etc.)
 */

package com.szymon.gameboy.cpu.utils;

public enum InsType
{
	NONE("NONE"),
	NOP("NOP"),
	LD("LD"),
	INC("INC"),
	DEC("DEC"),
	RLCA("RLCA"),
	ADD("ADD"),
	RRCA("RRCA"),
	STOP("STOP"),
	RLA("RLA"),
	JR("JR"),
	RRA("RRA"),
	DAA("DAA"),
	CPL("CPL"),
	SCF("SCF"),
	CCF("CCF"),
	HALT("HALT"),
	ADC("ADC"),
	SUB("SUB"),
	SBC("SBC"),
	AND("AND"),
	XOR("XOR"),
	OR("OR"),
	CP("CP"),
	POP("POP"),
	JP("JP"),
	PUSH("PUSH"),
	RET("RET"),
	CB("CB"),
	CALL("CALL"),
	RETI("RETI"),
	LDH("LD"),
	JPHL("JP"),
	DI("DI"),
	EI("EI"),
	RST("RST");
	
	private String szIns;
	
	private InsType(String value)
	{
		this.szIns = value;
	}
	
	public String getValue()
	{
		return this.szIns;
	}
}
