# /bin/bash

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

echo "Installing MongoDB..."
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6
echo "deb [ arch=amd64,arm64 ] http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.4 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.4.list
apt-get update
apt-get install -y mongodb-org-server
apt-get install -y mongodb-org-shell
apt-get install -y mongodb-org-tools
apt-get install -y mongodb-org-mongos
apt-get install -y mongodb-org

echo "Installing and updating pip..."
apt install -y python3-pip
pip3 install --updgrade

echo "Installing PyMongo..."
pip3 install pymongo

echo "Installing Flask..."
pip3 install flask

echo "Starting Mongo daemon..."
service mongod start
