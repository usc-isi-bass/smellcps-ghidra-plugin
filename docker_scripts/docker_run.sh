docker run \
    -it \
    --memory="600g" \
    --net=host \
    --platform linux/amd64 \
    -v /tmp/.X11-unix:/tmp/.X11-unix \
    -h smellcps-ghidra-plugin \
    -e DISPLAY=$(hostname):0 \
    -v $(pwd)/..:/home/user/repos/usc-isi-bass/smellcps-ghidra-plugin \
    --name smellcps-ghidra \
    smellcps-ghidra-plugin
