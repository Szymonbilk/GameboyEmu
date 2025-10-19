/**
 * @author 18bilkiewiczs
 * Enum to represent all the addressing modes of an instruction (e.g. fetch data from a register, store in another register etc.)
 */

package com.szymon.gameboy.cpu.utils;

public enum AddrMode
{
	NONE,
	R_D16,
	R_R,
	MR_R,
	R,
	R_D8,
	R_MR,
	R_HLI,
	R_HLD,
	HLI_R,
	HLD_R,
	R_A8,
	A8_R,
	HL_SPR,
	D16,
	D8,
	IMP,
	MR_D8,
	MR,
	A16_R,
	R_A16;
}
