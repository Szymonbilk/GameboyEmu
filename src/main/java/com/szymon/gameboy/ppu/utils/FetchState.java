/**
 * @author 18bilkiewiczs
 * Enum to represent the state of the pixel FIFO
 */

package com.szymon.gameboy.ppu.utils;

public enum FetchState 
{
	TILE,
	DATALOW,
	DATAHIGH,
	SLEEP,
	PUSH;
}
