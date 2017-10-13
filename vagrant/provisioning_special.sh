#!/usr/bin/env bash
echo "Start Special"
add-apt-repository -y ppa:ubuntu-desktop/ubuntu-make
aptitude update 
aptitude install -y ubuntu-make
echo "End Special"