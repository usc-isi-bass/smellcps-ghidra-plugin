from ubuntu:latest

ARG USER_ID
#ARG XAUTH_COOKIE

ENV TZ=America/Los_Angeles
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

arg DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get dist-upgrade -y
RUN apt-get update
RUN apt-get install -y ca-certificates
RUN apt-get install -y software-properties-common
RUN apt-get install -y python3
RUN apt-get install -y build-essential
RUN apt-get install -y git
RUN apt-get install -y cmake
RUN apt-get install -y wget
RUN apt-get install -y zip
RUN apt-get install -y curl
RUN apt-get install -y sudo
RUN apt-get install -y vim

RUN apt-get install -y autoconf
RUN apt-get install -y pkg-config
RUN apt-get install -y libtool
RUN apt-get install -y virtualenv
RUN apt-get install -y python3-dev
RUN apt-get install -y default-jre
RUN apt-get install -y default-jdk

RUN adduser --home /home/user/ --shell /bin/bash --disabled-password --uid $USER_ID user
RUN passwd --delete user
RUN usermod -aG sudo user

#Install gradle
WORKDIR /home/user
RUN mkdir /opt/gradle/
RUN wget -O gradle.zip https://services.gradle.org/distributions/gradle-7.3.3-bin.zip
RUN unzip -d /opt/gradle gradle.zip
ENV PATH="/opt/gradle/gradle-7.3.3/bin:$PATH"

USER user
WORKDIR /home/user

# Create the virtual environment
RUN virtualenv --without-pip -p /usr/bin/python3 angr
# Set PATH so that we first run the stuff in the venv
ENV PATH="/home/user/angr/bin:$PATH"
RUN curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
RUN python3 ./get-pip.py


RUN python3 -m pip install numpy
#RUN python3 -m pip install scipy
RUN python3 -m pip install matplotlib
RUN python3 -m pip install jsonpickle
RUN python3 -m pip install pygments 
RUN python3 -m pip install nose
RUN python3 -m pip install angr

RUN mkdir -p /home/user/repos/usc-isi-bass/
WORKDIR /home/user/repos/usc-isi-bass/
RUN git clone https://github.com/usc-isi-bass/math_to_symbexpr_map_generation.git
WORKDIR /home/user/repos/usc-isi-bass/math_to_symbexpr_map_generation
RUN git checkout dev/ghidra_plugin



WORKDIR /home/user
RUN wget -O ghidra.zip https://github.com/NationalSecurityAgency/ghidra/releases/download/Ghidra_10.1.1_build/ghidra_10.1.1_PUBLIC_20211221.zip
RUN unzip ghidra.zip
RUN rm ghidra.zip
RUN mv ghidra_* ghidra

WORKDIR /home/user
# Activate virtual env on startup
RUN sh -c 'echo "source /home/user/angr/bin/activate" >> /home/user/.bashrc'
