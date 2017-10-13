#!/usr/bin/env bash

# General Part
export DEBIAN_FRONTEND=noninteractive

echo "Start General"

# Add Package-Source for chrome
wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list'

# Install Packages
apt-get update
apt-get install -y aptitude
aptitude safe-upgrade -y
aptitude install -y apt-utils
aptitude install -y lxde
aptitude install -y yum git wget unzip google-chrome-stable gvfs-bin gitk konsole

cp -rf /vagrant/vagrant/copy/* /
chown -R vagrant /opt
mkdir -p /home/vagrant/Desktop
cd /home/vagrant/Desktop
ln -fs /usr/share/applications/lxterminal.desktop Terminal
ln -fs /usr/share/applications/google-chrome.desktop
mkdir -p /vagrant/localfiles/git
cd /home/vagrant
ln -s /vagrant/localfiles/git/git.config .gitconfig

# add swap
if [ ! -e /swapfile ]; then
        fallocate -l 8G /swapfile && chmod 0600 /swapfile && mkswap /swapfile && swapon /swapfile && echo '/swapfile none swap sw 0 0' >> /etc/fstab
        echo vm.swappiness = 10 >> /etc/sysctl.conf && echo vm.vfs_cache_pressure = 50 >> /etc/sysctl.conf && sysctl -p
fi

echo "End General"