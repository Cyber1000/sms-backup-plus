#!/usr/bin/env bash
echo "Start Special"
add-apt-repository -y ppa:ubuntu-desktop/ubuntu-make
aptitude update 
aptitude install -y ubuntu-make

cp /vagrant/localfiles/ssh/id_rsa* /home/vagrant/.ssh/
chmod 700 /home/vagrant/.ssh/id_rsa
echo "End Special"