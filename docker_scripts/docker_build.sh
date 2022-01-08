docker build --no-cache --build-arg USER_ID=$(id -u) --build-arg GROUP_ID=$(id -g) --rm . -f Dockerfile -t smellcps-ghidra-plugin
