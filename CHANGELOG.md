# Change Log

<!-- Template
## 'Version major.minor.patch' or 'Unreleased'
_yyyy-mm-dd_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/prev-tag-name...curr-tag-name)

### Breaking Changes
### Changed
### Added
* Line 1
* Line 2
-->

## Version 0.11.6
_2020-09-05_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/v0.11.5...v0.11.6)

### Added
* Full 1.16.2 support (#33)

## Version 0.11.5
_2020-06-26_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/v0.11.4...v0.11.5)

### Changed
* Added a message when a player starts sitting
* Using own bStats-Page now
  ([https://bstats.org/plugin/bukkit/BetterChairs%20Remastered/8214](https://bstats.org/plugin/bukkit/BetterChairs%20Remastered/8214))
* Made some changes to GitHub Actions
* Only deploy betterchairs-api as other modules are not relevant

## Version 0.11.4
_2020-06-26_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/v0.11.3...v0.11.4)

### Changed
* Fix Chairs not spawning (Some plugins prevented ArmorStands from spawning)


## Version 0.11.3
_2020-06-26_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/v0.11.2...v0.11.3)

### Added
* Full Spigot 1.12 support

### Changed
* Fix `Unsupported API Version` error when running in versions older than 1.16
* Fix `ClassCastException` caused by wrong imports


## Version 0.11.2
_2020-06-26_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/v0.11.1...v0.11.2)

### Added
* Full Spigot 1.16 support
* Deploy to GitHub packages (pom.xml)

## Version 0.11.1
_2020-06-26_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/v0.11.0...v0.11.1)

### Breaking Changes
* **All the packages and classes have been replaces and changed**
* The whole project has been recoded and redesigned with full-version support in mind (1.8 and newer)


## Version 0.11.0
_2020-05-12_ [GitHub Diff](https://github.com/Sprax2013/BetterChairs/compare/v0.10.2...v0.11.0)

### Breaking Changes
I did my best to make sure the resulting .jar-file keeps its class and package structure.
**But no guarantee!**

### Changed
* Using maven to compile the project (#3)

### Added
* GitHub Actions to automatically compile PR to master
* README.md (#9)
* CHANGELOG.md (#9)
* LICENCE (#9)
* docs/version.txt to be used for the Updater ([BlackScarx/BetterChairs #13](https://github.com/BlackScarx/BetterChairs/pull/13))

## Version 0.10.2
This is the starting point of this repository as this is a fork of [BlackScarx/BetterChairs](https://github.com/BlackScarx/BetterChairs).