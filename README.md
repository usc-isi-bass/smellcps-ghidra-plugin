# smellcps-ghidra-plugin

## Setup
### With Docker
We recommend you develop and run the plugin inside a Docker image, using the scripts provided in `docker_scripts`.
1. Use the scripts `docker_build.sh` and `docker_run.sh` to build and run the environment, respectively.
1. Inside the docker image, use the script `build_plugin.sh` to build the project using Gradle.
1. The plugin can then be installed using the `install_plugin.sh` script.
1. Ghidra can be run with the usual `ghidraRun` script, found in `/home/user/ghidra/`.

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
 
