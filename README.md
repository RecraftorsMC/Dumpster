# Dumpster

Allows you do dump, at will
* your game's registries, with the command `/dump registries`
* your game's data, including tags, recipes and more, with the command `/dump data`
* more specific data, such as either tags, recipes, loot tables, etc, with the command `/dump data <type>`
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

### How to implement compatibility

You may import this mod in your Gradle project using the Modrinth maven API, as per indicated in [the documentation](https://docs.modrinth.com/maven).

You may then implement the [RecipeJsonParser](https://github.com/RecraftorsMC/Dumpster/blob/main/src/main/java/mc/recraftors/dumpster/recipes/RecipeJsonParser.java) interface in a class and register it as a `recipe-dump` entry-point as shown in the base [fabric.mod.json](https://github.com/RecraftorsMC/Dumpster/blob/dbb8bbc4d9bc2516854b2e88fca182c137aad875/src/main/resources/fabric.mod.json#L24) file.

You shall also annotate it with the [TargetRecipeType](https://github.com/RecraftorsMC/Dumpster/blob/main/src/main/java/mc/recraftors/dumpster/recipes/TargetRecipeType.java) annotation, in order to indicate what recipe you are actually registering it for. You may as well indicate a priority value, if you want to overhaul another parser, by providing it a higher value.

And that is all you have to do.

All in all, your recipe dump class shall resemble the following (using Yarn mappings):
```java
import com.google.gson.JsonObject;
import mc.recraftors.dumpster.recipes.RecipeJsonParser;
import mc.recraftors.dumpster.recipes.TargetRecipeType;
import net.minecraft.recipe.Recipe;

@TargetRecipeType("mymod:myrecipetype")
public class MyRecipeTypeJsonParser implements RecipeJsonParser {
    private MyRecipe recipe;

    @Override
    public JsonParser.InResult in(Recipe<?> recipe) {
        if (recipe instanceof MyRecipe myRecipe) {
            this.recipe = myRecipe;
            return RecipeJsonParser.InResult.SUCCESS;
        }
        return RecipeJsonParser.InResult.FAILURE;
    }

    @Override
    public JsonObject toJson() {
        // implement your parsing method here
    }
}
```

Other methods are optionally available, for in-detail functionalities, such as regrouping multiple recipes generated out of a single files, or supporting alternative recipe type IDs
