## GameboyEmu Java Gameboy Emulator
This is a Java Gameboy emulator that I wrote as an exploration into how computer systems work by simply emulating one.
It is by no means perfect, and the code isn't the best quality (I did write this over a year ago as of today).

## Running the code
The project has been built with Gradle. Simply install all the files, and run "gradle run" to start the emulator.
**Note:** the emulator expects a file by the name of "tetris.gb" in a folder called "roms" at the top level of the directory (e.g. at the same level as the .gradle folder).
Without this, the code breaks, though I am unable to provide the ROM, though you may obtain a ROM by dumping it from your own cartridge.

When the emulator is running, WASD keys correspond to the D-Pad, O and P to B and A, and K and L to Select and Start respectively.
Right clicking brings up a menu which allows you to select a ROM file from elsewhere on your computer, toggle the tile viewer (which shows all of the tiles currently stored in the Gameboy's memory), and changing the colours used for output.
