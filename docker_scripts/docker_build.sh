docker build \
    --no-cache \
    --build-arg USER_ID=$(id -u) \
    --platform linux/amd64 \
    --rm . \
    -f Dockerfile \
    -t smellcps-ghidra-plugin
