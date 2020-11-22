#!/usr/bin/env python3
import sys


"""
a fuzzer for the sdns udp server
to test its performance in various incorrect formatted query data

usage:
python sdns_fuzzer.py serverip  port
where serverip and port are command arguments
serverip is the ip of sdns server
port is the port of sdns server
"""
from boofuzz import *

if len(sys.argv) < 3:
    sys.exit(-1)

s_initialize("query")
if s_block_start("header"):
    s_word(123, name="id")
    s_byte(1)
    s_byte(0)
    s_byte(0)
    s_byte(1)
    s_word(0, name="AnCount", endian=">")
    s_word(0, name="NsCount", endian=">")
    s_word(0, name="ArCount", endian=">")
s_block_end()

if s_block_start("question"):
    if s_block_start("qname"):
        s_byte(6)
        s_static("google")
        s_byte(3)
        s_static("com")
        s_byte(0)
    s_block_end()
    s_word(0x00ff, endian=">")
    s_word(0x0001, endian=">")

s_block_end()

sess = Session(target=Target(connection=UDPSocketConnection(sys.argv[1], int(sys.argv[2]))))
sess.connect(s_get("query"))

sess.fuzz()
