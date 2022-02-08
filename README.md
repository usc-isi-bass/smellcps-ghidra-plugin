# smellcps-ghidra-plugin

## Setup
### With Docker
We recommend you develop and run the plugin inside a Docker image, using the scripts provided in `docker_scripts`.   
For macOS please also read the [next section](#running-on-macos) before running the docker image.
1. Use the scripts `docker_build.sh` and `docker_run.sh` to build and run the environment, respectively.
1. Inside the docker image, use the script `build_plugin.sh` to build the project using Gradle.
1. The plugin can then be installed using the `install_plugin.sh` script.
1. Ghidra can be run with the usual `ghidraRun` script, found in `/home/user/ghidra/`.

#### Running on macOS
We need to install X Window [XQuartz](https://www.xquartz.org/) to run the Ghidra GUI from Docker.

First, install the latest XQuartz with either by brew or [from the website](https://www.xquartz.org/).
```
$ brew install --cask xquartz
```
Next, start Xquartz from your terminal with the following command. 
```
$ open -a XQuartz
```
After starting Xquartz, in the app go to `Perferences > Security` and enable `Allow connections from network clients`.

Next, add the local network to X11 access control list with **xhost**. If you install Xquartz from brew, you can find **xhost** in `/opt/X11/bin/xhost`.
```
$ DISPLAY=$(hostname):0 /<path-to-xhost>/xhost + $(hostname)
```
Now you can continue to run the `docker_run.sh` script to start the image.


### Without Docker
1. Update `smellcps_plugin_config.properties` such that:
    1. `python3` points to the `python` binary in your angr virtual environment.
    1. `driver` points to the `driver_for_ghidra.py` file in your clone of the [math_to_symbexpr_map_generation](https://github.com/usc-isi-bass/math_to_symbexpr_map_generation) repository.

Move `smellcps_plugin_config.properties` to `$HOME/.ghidra/`
##### With Eclipse
1. Install the GhidraDev Eclipse plugin by following the instructions [here](https://ghidra-sre.org/InstallationGuide.html#Extensions).
1. From within GhidraDev in Eclipse, create a new Ghidra Module Project.
1. Merge this repository with the root of the new project.


##### Without Eclipse
Run `GHIDRA_INSTALL_DIR=/path/to/ghidra/install/dir gradle`.
This should create a `dist/*.zip` archive.
This ZIP archive can be installed as a Ghidra extension, as described [here](https://ghidra-sre.org/InstallationGuide.html#Extensions).
If the plugin window does not show, it can be reopened in the `Window` menu.
 
