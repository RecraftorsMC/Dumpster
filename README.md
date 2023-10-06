# Dumpster

Allows you do dump, at will
* your game's registries, with the command `/dump registries`
* your game's data, including tags and recipes, with the command `/dump data`
* both of the above, with the pure `/dump` command

## Config

Includes a small configuration file, allowing you to set whether or not to dump the tags and/or the recipes when dumping the data. The same way, you can configure whether to dump data and/or registries upon reload or startup.
It also gives you the option to control how should the dump files be organized.

This file can be found as `dumpster.properties` file, in the config folder.

## Server or Client

This mod has actually been thought somewhat smartly! (I surprise even myself)

So, when the mod is on the server, OP players only have the option to use the `/dumpster` command, which will dump the wanted data *in the server's files*!

And just as well, when the mod is on the client, the player will be able to use the `/dumpster` command without any concern of whether they are OP or not.

Last but not least, if the mod is on both the client and the server, due to Minecraft's architecture, the client-side command will be renamed `/dump-client`, in order to work properly.

## Use cases

This mod is not meant to be used for regular gaming! It is solely intended as a handy tool for datapack, mod and modpack creators, for them to be able to know whether their game manipulations had a proper impact on the game.

## Compatibility

This mod is not able to process modded  recipes by default. Thus, a compatibility layer must be implemented, either on the side of the mod adding the recipe type, or by some third-party mod that intends to add it.
