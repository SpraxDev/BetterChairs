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

## Unreleased
<!-- _yyyy-mm-dd_ [GitHub Diff](https://github.com/SpraxDev/BetterChairs/compare/v1.2.0...curr-tag-name) -->

### Changed
* Updated dependency `item-nbt-api` from `2.6.0` to `2.6.1`
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