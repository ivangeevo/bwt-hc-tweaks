## v1.5(dev)
+ Added new items: Fuse & Blasting Oil
+ Changed the Dynamite recipe from BWT to require fuse and blasting oil
+ Changed the vanilla recipe for TNT to require a barrrel, fuse and more gunpowder (no sand)
+ Fixed/removed the old texture for the top part of the hemp to only use the new textures that were added since BWT 2.0

## v1.4.5
+ Fixed the game crashing when planting hemp crop blocks while using older versions of BTWR: Shared Library
+ Updated the mod to Fabric API 0.116.9, Fabric Loader 0.18.4 & BTWR: Shared Library 0.8.2

## v1.4.4
+ Fixed missing translations for text in Mod Menu
+ Changed the mod internally so that now newer versions of BWT should normally work with the mod if they are compatible
+ Updated the mod to Better With Time 2.0.2

## v1.4.3
+ Changed all configuration options in the mod to use the custom config library added by BTWR: Shared Library. This fixes the bug from last version that crashed the game without any warnings of the missing library that created the configurations
+ Removed Supermartijn642's config lib as the one creating configuration setting as it requires itself as a dependency to work properly
+ Updated the mod to BTWR: Shared Library 0.6.5

## v1.4.2
+ Added a new configuration option for setting the sawing speed of the Saw Block. It defaults to 20 ticks which is the increased amount from retail BTW. 
+ Changed all configuration options setting to be handled with Supermartijn642's Config Lib internally instead of Cloth Config API, which is used only for client side config options.
+ Refactored pretty much the whole code; mainly for readability and cleaning up, but also so it's more in order with other mods from the BTWR project
+ Updated the mod to BTWR Shared Library 0.6.4

## v1.4.1
+ Updated the mod to Better With Time 2.0.1

## v1.4
+ Updated the mod to Better With Time 2.0!
+ Added the following changes and configuration options via Mod Menu for:
1. Revert Buddy Block neighbor update logic to the original BTW behavior
2. Block Dispensers requiring strong redstone power
+ Fixed the hemp plant to drop its top part when the bottom one is broken with a piston push.
+ Also updated the mod to Fabric API 0.116.7, Fabric Loader 0.17.3 & BTWR Shared Library 0.62

## v1.3
+ Added a PistonBreakEvent & callbacks that hook when a block is broken by a piston push.
+ Changed drops for a hemp block to work better and fixed the top block to get destroyed instead of pushed.
+ Fixed hemp crop block to be able to grow on blocks that do not require moisture like the soil planter (ALWAYS_FERTILE_SOIL)
+ Updated the mod to Fabric API 0.116.4 & BTWR Shared Library 0.56

## v1.2
+ Made the hemp plant block to drop loot when broken with a piston push
+ Made soul urns and moulds to be considered solid blocks, and this makes them stay in place when hit by water, which in turn allows easy automation with a tube kiln setup.
+ Fixed hemp plant to also drop its top part loot when the bottom is broken 
+ Changed(slightly increased) the time it takes for a Saw Block to break other blocks to match the speed that's in retail BTW
+ Updated the mod to Fabric API 0.116.2, Fabric Loader 0.16.14 & BTWR Shared Library 0.55

## v1.1
+ Updated the mod to work with Better With Time version 1.9.4
+ Updated the mod to Fabric API 0.115.6, Fabric Loader 0.16.13 & BTWR Shared Library 0.51

## v1.0.2
+ More fixes for the hemp plant; Changed some blockstate models to display properly and fixed some random tick logic.
+ Added the Dormant Soul Forge to the "Natural" creative tab category.

## v1.0.1
+ Fixed a bug where the hemp plant was growing its top part too fast

## v1.0
+ Initial release