# Change Log

<!-- Template
## 'Version major.minor.patch' or 'Unreleased'
_yyyy-mm-dd_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/prev-tag-name...curr-tag-name)

### Added
### Breaking Changes
### Changed
* Line 1
* Line 2
-->
## Version 1.7.0
_2023-06-14_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.6.0...v1.7.0)

### Added
* Minecraft 1.20 / 1.20.1 support


## Version 1.6.0
_2023-03-17_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.5.6...v1.6.0)

### Added
* Minecraft 1.19.4 support
### Changed
* Player no longer fall through the chair block when `TeleportPlayerToOldLocation` is disabled
### Breaking Changes
* `Chair#getPlayerLeavingLocation()` now takes `TeleportPlayerToOldLocation` into account
  and produces a different result when it is disabled
  * Should *probably* not cause any trouble for other plugins using the API


## Version 1.5.6
_2023-01-20_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.5.5...v1.5.6)

### Changed
* Fixes specifying custom blocks in the config not working
  * Only stairs and chairs were able to work, no matter what you specified


## Version 1.5.5
_2023-01-08_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.5.4...v1.5.5)

### Changed
* config.yml and message.yml are now properly read in UTF-8 on all systems
  * This allows non-english languages to be used properly
* Updated download links to the Songoda Marketplace to use the new URL


## Version 1.5.4
_2023-01-03_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.5.3...v1.5.4)

### Added
* Minecraft 1.19.3 support
* New configuration option to allow seat-switching while sitting
  * It is disabled by default
### Changed
* Fixes updater permission description not being declared correctly


## Version 1.5.3
_2022-08-24_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.5.2...v1.5.3)

### Added
* Minecraft 1.19.1 and 1.19.2 support
### Changed
* The Minecraft 1.19 submodules now use the remapped Spigot
  * This makes it easier to create updates for new Spigot versions


## Version 1.5.2
_2022-06-19_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.5.1...v1.5.2)

### Added
* Minecraft 1.19 support


## Version 1.5.1
_2022-03-22_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.5.0...v1.5.1)

### Added
* Minecraft 1.18.2 support
* Minecraft 1.13 support (it always ran in legacy mode by accident)
* The API has been updated to allow chair creating with a custom `yOffset` (`ChairManager#create(Player, Block)`)
* Your feet now point in the direction you are looking! (Already worked in pre Minecraft 1.13; Now works on all supported versions)
### Changed
* Errors caused by the NBT-API library are handled more extensively
  (if you are running an unsupported server version, you should have less trouble doing so now)
* Plugin version is now part of the file name


## Version 1.5.0
_2021-12-21_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.4.0...v1.5.0)

### Added
* Minecraft 1.18 support
* Setting to disable permission check for regeneration effect (#108)
* *PlaceholderAPI* support with two placeholders (#114)
* Plugin description in `plugin.yml`
### Breaking Changes
* Changed method signature from `ChairManager#hasChairsDisabled(Player)` to `ChairManager#hasChairsDisabled(OfflinePlayer)`
* Renamed myself from `Sprax2013` to `SpraxDev` in `plugin.yml`
### Changed
* Uses *ArmorStand*s instead of *Arrow*s for chairs by default again
* Apply `NoGravity` to spawned Arrow (#111, #116)
* Moves spawned arrow further into the chair block (#111)
* Updated dependencies `XSeries`, `Item-NBT-API` and `annotations`


## 1.4.0
_2021-08-16_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.4.0-ALPHA...v1.4.0)

### Added
* Option to use ArmorStands or Arrows as ArmorStands proofed to be even less reliable across versions
  * If you are running 1.8 (or maybe even up to 1.15), you might want to enable AmorStands 
    in the `config.yml` as you can see the Arrow
### Changed
* Sitting on Trapdoors now places you on the correct height
* Using `/sit` sometimes showed *no permission* message
* Updated dependencies `XSeries` and `annotations`
* Fixed bStats (broke in 1.4.0-ALPHA)


## Version 1.4.0-ALPHA
_2021-06-15_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.3.0...v1.4.0-ALPHA)

### Added
* Added support for Minecraft 1.17 (and Java 16)
* Added `/sit` command allowing players to sit on the ground
  * Permission `BetterChairs.cmd.sit` is automatically granted to all players
* Added support for Spigot v`1.13.0` (`v1_13_R1`)
### Breaking Changes
* Methods in the API now return `Entity` instead of `ArmorStand` because Spigot 1.17 uses Arrows
  * This allows for more flexibility in the implementation
  * Additional related changes might be introduced when leaving the `ALPHA` version
### Changed
* Changed the code around `config.yml` and `messages.yml` and how older versions of them are upgraded
* Updated dependency `item-nbt-api` from `2.7.1` to `2.8.0`
* Updated dependency `XSeries` from `7.6.0` to `8.0.0`
* Updated dependency `annotations` from `20.1.0` to `21.0.1`
* Updated dependency `bstats-bukkit` from `1.8` to `2.2.1`
* Updated dependency `lime-spigot-api` from `0.0.1-SNAPSHOT` to `0.0.2-SNAPSHOT`
* Using Spigot v`1.11.2` instead of v`1.11` to compile
* Using Spigot v`1.9.2` instead of v`1.9` to compile


## Version 1.3.0
_2021-01-22_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.2.0...v1.3.0)

### Added
* You can configure BetterChairs to remember players who disabled chairs
  by enabling `Chairs.RememberIfPlayerDisabledChairsAfterRelogin` in `config.yml` (#77)
* **API**: You can now get and set the current status for a player by providing a UUID (e.g. for offline players) (#77)
### Changed
* Don't check off-hand when requiring empty hands to sit (#89)
* Fix NBT-API checking for updates (A bot is regularly checking for updates, don't worry ^^) (#89)
* Use Spigot 1.16.5 instead of 1.16.4 for compiling (No changes to nms classes)
* Fix `IllegalArgumentException` that could sometimes occur when a player is teleported
  after interacting with a block (#81) (#83)
* `README.md` now contains the download links to GitHub, Songoda and SpigotMC
* Updated dependency `item-nbt-api` from `2.6.0` to `2.7.1`
* Updated dependency `bstats-bukkit` from `1.7` to `1.8`


## Version 1.2.0
_2020-11-08_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.1.0...v1.2.0)

### Added
* Full 1.16.4 support (#68)
* bStats: Add 'NMS Version' chart
### Breaking Changes
* Updated `item-nbt-api` (2.5.0->2.6.0) and `XSeries` (7.5.4->7.6.0)
### Changed
* Update notifications (chat and console), do now contain a link to the `CHANGELOG.md` (#56)
* A block from a newer version given to the block filter, will no longer be tried to be automatically replaced with another one


## Version 1.1.0
_2020-10-19_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.0.0...v1.1.0)

### Added
* Allow blocking/allowing specific blocks to be used as chairs (#57)
* Add options to disallow air below/above chairs (#58)
* Updater now shows download links from GitHub, Songoda and SpigotMC
### Changed
* Improve console logging
* Improve the updater's version detection (`v1.1.0-SNAPSHOT` is now detected as older than `v1.1.0`)


## Version 1.0.0
_2020-10-15_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.13.0...v1.0.0)

### Added
* Add option to ignore other plugins disallowing a player to use a chair (e.g. PlotSquared or WorldGuard) (#53)
* Automatically generate JavaDocs (#55)
* Add documentation (#8)
### Changed
* Fix error when trying to create `config.yml` or `messages.yml` on fresh installs
* README.md: Added commands and permissions
* README.md: Added differences compared to the original plugin (#22)


## Version 0.13.0
_2020-10-09_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.12.0...v0.13.0)

### Breaking Changes
* BetterChairs is now compiled using Java 11 - It *should* still work on servers running Java 8 (create an issue if not) (#50)
* The configuration system has been rewritten and replaced with a new one ([SpraxDev/LimeDevUtility](https://github.com/SpraxDev/LimeDevUtility)) (#52)
### Changed
* `config.yml` and `messages.yml` do now contain comments to explain all the settings (#52)
* The dependency XSeries has been updated and might have improved performance (probably nothing you'd be able to notice)
* NBT-API is longer logging information to the console (It sometimes still did) (#51)


## Version 0.12.0
_2020-09-24_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.11.6...v0.12.0)

### Added
* Using `ItemNBTAPI` by @tr7zw to protect ArmorStands across versions (#36)
### Breaking Changes
* Using Maven default target/output directory
### Changed
* GitHub Actions: Improved caching
* Updated dependency XSeries from 7.2.1.1 to 7.3.1
* Updated the README.md


## Version 0.11.6
_2020-09-05_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.11.5...v0.11.6)

### Added
* Full 1.16.2 support (#33)


## Version 0.11.5
_2020-06-26_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.11.4...v0.11.5)

### Changed
* Added a message when a player starts sitting
* Using own bStats-Page now
  ([https://bstats.org/plugin/bukkit/BetterChairs%20Remastered/8214](https://bstats.org/plugin/bukkit/BetterChairs%20Remastered/8214))
* Made some changes to GitHub Actions
* Only deploy `betterchairs-api` as other modules are not relevant


## Version 0.11.4
_2020-06-26_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.11.3...v0.11.4)

### Changed
* Fix Chairs not spawning (Some plugins prevented ArmorStands from spawning)


## Version 0.11.3
_2020-06-26_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.11.2...v0.11.3)

### Added
* Full Spigot 1.12 support
### Changed
* Fix `Unsupported API Version` error when running in versions older than 1.16
* Fix `ClassCastException` caused by wrong imports


## Version 0.11.2
_2020-06-26_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.11.1...v0.11.2)

### Added
* Full Spigot 1.16 support
* Deploy to GitHub packages (pom.xml)


## Version 0.11.1
_2020-06-26_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.11.0...v0.11.1)

### Breaking Changes
* **All the packages and classes have been replaces and changed**
* The whole project has been recoded and redesigned with full-version support in mind (1.8 and newer)


## Version 0.11.0
_2020-05-12_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v0.10.2...v0.11.0)

### Added
* GitHub Actions to automatically compile PR to master
* README.md (#9)
* CHANGELOG.md (#9)
* LICENCE (#9)
* docs/version.txt to be used for the Updater ([BlackScarx/BetterChairs #13](https://github.com/BlackScarx/BetterChairs/pull/13))
### Breaking Changes
I did my best to make sure the resulting .jar-file keeps its class and package structure.
**But no guarantee!**
### Changed
* Using maven to compile the project (#3)


## Version 0.10.2
This is the starting point of this repository as this is a fork of [BlackScarx/BetterChairs](https://github.com/BlackScarx/BetterChairs)
