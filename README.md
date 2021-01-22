<p align="center">
  <img src="./modules/betterchairs-plugin/src/main/resources/icon.png">
</p>

<p align="center">
  <a href="https://JavaDocs.Sprax2013.de/BetterChairs/">
    <img alt="JavaDocs" src="https://img.shields.io/badge/JavaDocs-latest-succes?logo=Java">
  </a>
  <a href="https://sprax.me/discord">
    <img alt="Get Support on Discord" src="https://img.shields.io/discord/344982818863972352.svg?label=Get%20Support&logo=Discord&color=blue">
  </a>
  <a href="https://www.patreon.com/sprax">
    <img alt="Support me on Patreon"
         src="https://img.shields.io/badge/-Support%20me%20on%20Patreon-%23FF424D?logo=patreon&logoColor=white">
  </a>
</p>

<p align="center">
  <a href="https://github.com/SpraxDev/BetterChairs/actions?query=workflow%3A%22Build+with+Maven%22">
    <img alt="Build with Maven" src="https://github.com/Sprax2013/BetterChairs/workflows/Build%20with%20Maven/badge.svg">
  </a>
  <a href="https://sonarcloud.io/dashboard?id=SpraxDev_BetterChairs">
    <img alt="Quality Gate Status"
         src="https://sonarcloud.io/api/project_badges/measure?project=SpraxDev_BetterChairs&metric=alert_status">
  </a>
</p>

# BetterChairs (Remastered)
BetterChairs allows you and your players to sit on chairs. It's that simple!
Every stair and every half block can be a chair if you want it to!

It is:
* **fast**, as it has been written with performance in mind
* **easily updated**, because you get notified about new versions, and your configurations get upgraded automatically
* **customizable**, as it generates configuration files for you
* **reliable**, as it supports all Minecraft versions newer than 1.8 natively
* **free** and OpenSource. Request Features or add them yourself!


![Screenshot of players sitting on chairs](./docs/images/Screenshot4.jpg)


## Commands & Permissions
|        Command        |              Permission              | Description               |
|:---------------------:|:------------------------------------:|:-------------------------:|
| -                     | `BetterChairs.use` (default: `true`) | Allows players to sit on chairs |
| `/BetterChairs <toggle\|on\|off\|status>` | `BetterChairs.cmd.toggle` (default: `true`) | Allows individual players to disabled chairs for them (aliases: `/toggleChairs`, `/bct`) |
| `/BetterChairs reset` | `BetterChairs.cmd.reset`             | Allows admins to eject all players from their chairs |
| `/BetterChairs reload`| `BetterChairs.cmd.reload`            | Allows admins to reload the plugin's configuration files |
| -                     | `BetterChairs.updater`               | Notifies players about updates when joining the server |


## Useful Links
* Download the latest version from [GitHub](https://github.com/SpraxDev/BetterChairs/releases/latest),
  [Songoda](https://songoda.com/marketplace/product/betterchairs-remastered-by-spraxdev.489),
  [SpigotMC](https://www.spigotmc.org/resources/betterchairs-remastered.84809/)
* [The Wiki](https://github.com/SpraxDev/BetterChairs/wiki) contains the **API Documentation**

[![bStats Graph](https://bstats.org/signatures/bukkit/BetterChairs%20Remastered.svg)](https://bstats.org/plugin/bukkit/BetterChairs%20Remastered/8214)

* Original Project (No activity):
  [GitHub](https://github.com/BlackScarx/BetterChairs),
  [SpigotMC](https://www.spigotmc.org/resources/better-chairs.18705/),
  [bStats](https://bstats.org/plugin/bukkit/BetterChairs/768)


## BetterChairs API (+ Events)
Please take a look at [the documentation](https://github.com/SpraxDev/BetterChairs/wiki/BetterChairs-API) and
the [JavaDocs](https://JavaDocs.Sprax2013.de/BetterChairs/).


## What are the differences to the original version?
* Active author fixing bugs, adding features and add version support
* Support all versions newer than 1.8 (and in theory some older versions too)
* **All** stairs and slabs are supported in all versions
  * Have **every** block you like to act as a chair *(Experimental)*
* You can sit on slabs that are placed in the upper-half of a block
* A working Update-Notifier that won't send garbage messages if the Updater fails to check for an update
* Check if Signs are really attached to the chair (Requiring signs can be enabled in `config.yml`)
* This version is less aggressive when a player tries to sit on an chair (Can be partly re-enabled in `config.yml`)
* The content of `config.yml` and `messages.yml` has been restructured (old files are automatically converted)
* Have a world whitelist **or** blacklist
* Well documented API
* Fully rewritten code (Improves: Maintainability, Performance, Readability)
  * Moved to Maven and configured GitHub Actions to automatically compile the project


## Why did you create *BetterChairs (Remastered)*?
BetterChairs started in 2016 on SpigotMC and has been developed by
[BlackScarx](https://github.com/BlackScarx). Some years later,
I made my first commits to the project by adding custom Spigot-Events I wanted to use.

I have been granted write-access to that repository afterwards but could not contact
BlackScarx and created this fork to fit my needs and provide support for new Minecraft version.

My fork quickly made some big changes, and I decided to fully recode and redesign this project.
I started with moving to Maven and deleting all those `spigot.jar`s from the repository.

Because of that I rewrote the commit history and detached my Fork on GitHub to be no longer displayed as one
(always said *'27 commits behind BlackScarx:master'* because the commit hashes changed).

Today, I unofficially took over the project as soon as BlackScarx got inactive and am now maintaining this repository.