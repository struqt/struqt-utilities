# Introduce an Optimized Data Packet for Transmission or Storage


This document defines a kind of data packet structure that can apply compression
algorithms, encryption and decryption algorithms and signature algorithms.

It can be treated as a basic protocol between two endpoints in a network
that require secure communication and or as a file format to store secret information.

| Version   | Revision | Date        |
|:---------:|:--------:|:-----------:|
| -         | 1        | 2018-11-01  |


-----
### Basic Features

* Prevent data packet from being tampered with
* Support symmetric encrypted data to prevent raw data from being snooped
* Support simple clear text data
* Improve forward and backward compatibility between different revisions
* Reduce the byte size of the encoded packet
* Support more data formats
* Allow integration of other symmetric encryption algorithms
* Allow integration of other signature algorithms
* Allow integration of other signature algorithms
* Allow integration of other data compression algorithms


-----
### Definition of the data packet

| No. | Name             | Required | Data Type  | Sample Values     | Summary
| ---:|:---------------- |:--------:|:----------:|:----------------- |:---------------------------------------------
|  1  | start            | yes      | int32      | 0xCE              | Starting mark, a constant
|  2  | revision         | yes      | int32      | 0x01 - 0x7F       | Revision number
|  3  | nonce            | yes      | int32      | 0x00 - 0xFFFF     | Random number for data encryption
|  4  | options          | yes      | int64      | 0x00 - 0x7FFFFFFF | Option bits
|  5  | format           | yes      | int32      | 0x00 - 0xFFFF     | Raw data `format` enumeration
|  6  | length           | no       | int32      | 0x00 - 0x7FFFFFFF | Raw data length, absent when `format` is 0
|  7  | compress         | no       | int32      | 0x00 - 0xFF       | Configuration ID for data compression, absent when `0x01 & options` is 0
|  8  | crypto           | no       | int32      | 0x00 - 0xFF       | Configuration ID for data encryption, absent when `0x10 & options` is 0
|  9  | signId           | no       | int32      | 0x00 - 0xFF       | Configuration ID for signature of this packet,, absent when `0x0100 & options` is 0
| 10  | dataLength       | no       | int32      | 0x00 - 0x7FFFFFFF | Length of data, absent when `format` is 0
| 11  | data             | no       | byte[]     | 0x00 - 0xFF ...   | Payload bytes, absent when `dataLength` is 0
| 12  | signature        | no       | byte[]     | 0x00 - 0xFF ...   | Signature bytes, absent when `signId` is 0


-----
### How to encode data bytes?

```
raw --> compressed --> encrypted --> signed
```


-----
### How to decode data bytes?


```
checked --> decrypted --> decompressed --> raw
```
