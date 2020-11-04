# install

Boofuzz requires Python 2.7 or ≥ 3.5. Recommended installation requires pip. To ensure forward compatibility, Python 3 is recommended. As a base requirement, the following packages are needed:

Ubuntu/Debian
sudo apt-get install python3-pip python3-venv build-essential
OpenSuse
sudo zypper install python3-devel gcc
CentOS
sudo yum install python3-devel gcc

install boofuzz with pip

pip install boofuzz

run it

python sdns_fuzzer.py serverip  port
where serverip and port are command arguments
serverip is the ip of sdns server
port is the port of sdns server

# TESTING

test case
Header
header with various id
header with illegal qr opcode aa tc rd ra z rcode
header with an,ns,arCount which is not 0

question
invalid qname encode
