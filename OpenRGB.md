# OpenRGB

OpenRGB is an open-source lighting control software that allows you to control RGB lighting on various devices such as motherboards, RAM modules, and peripherals. It provides a unified interface to manage and synchronize RGB lighting across multiple brands and models.

## Installation

To install OpenRGB, follow these steps:

1. Download the latest release from the [OpenRGB GitHub repository](https://github.com/CalcProgrammer1/OpenRGB/releases).
2. Extract the downloaded ZIP file to a location on your computer.
3. Run the `OpenRGB.exe` executable file.

## Usage with Loewe Fabric Client

Once you have OpenRGB installed, you have to add the **"HTTP Hook"** plugin to OpenRGB (For instructions see [here](https://gitlab.com/OpenRGBDevelopers/OpenRGBHttpHookPlugin)). 

In the plugin you have to configure the hooks, the Loewe mod needs one named **"gruen"**, it should be to green lightning and one named **"rot"** with red lightning. 
Set the port from the plugin to **6742**, ideally also start the **SDK Server** of OpenRGB with the port **6743**. 

The last thing you have to do is start the two **.jar Files** (in a .zip file) you can download from [here](https://github.com/LoeweGuckmal/normal-Mod/releases).
The mod will integrate with OpenRGB to provide synchronized lighting effects based on in-game events and actions.