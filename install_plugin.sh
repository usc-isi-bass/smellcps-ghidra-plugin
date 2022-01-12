unzip -d /home/user/ghidra/Ghidra/Extensions/ $(ls -t dist/* | head -n1)
cp smellcps_plugin_config.properties ~/.ghidra/smellcps_plugin_config.properties
