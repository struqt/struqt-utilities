## Specification of an Optimized Data Packet for Transmission or Storage

This document defines a kind of data packet structure that can apply
compression algorithms, encryption and decryption algorithms and
signature algorithms.

It can be treated as a basic protocol between two endpoints in a network
that require secure communication and or as a file format to store
secret information.

| Revision | Date        |
|:--------:|:-----------:|
| 1        | 2018-11-01  |


----
#### Basic Features

* Prevent data packet from being tampered with
* Support symmetric encrypted data to prevent raw data from being snooped
* Support simple clear text data
* Improve forward and backward compatibility between different revisions
* Reduce the byte size of the encoded packet
* Support more data formats
* Allow integration of other symmetric encryption algorithms
* Allow integration of other signature algorithms
* Allow integration of other data compression algorithms


----
#### Definition of the data packet

| No. | Name             | Optional | Data Type  | Sample Values   | Summary
| ---:|:---------------- |:--------:|:----------:|:--------------- |:---------------------------------------------
|  1  | start            | no       | int32      | 0xCE            | starting mark, a constant
|  2  | revision         | no       | int32      | 0x01-0x7F       | revision number
|  3  | options          | no       | int32      | 0x00-0x7FFFFFFF | option bits
|  4  | nonce            | yes      | int32      | 0x00-0xFFFF     | absent when `0x01 & options` is 0. a random number
|  5  | sign             | yes      | int32      | 0x00-0xFF       | absent when `0x02 & options` is 0. configuration ID for signature of this packet
|  6  | compress         | yes      | int32      | 0x00-0xFF       | absent when `0x04 & options` is 0. configuration ID for data compression
|  7  | crypto           | yes      | int32      | 0x00-0xFF       | absent when `0x04 & options` is 0. configuration ID for data encryption
|  8  | format           | yes      | int32      | 0x00-0xFFFF     | absent when `0x04 & options` is 0. format of raw data
|  9  | raw              | yes      | int32      | 0x00-0x7FFFFFFF | absent when `0x04 & options` is 0. length of raw data bytes
| 10  | size             | yes      | int32      | 0x00-0x7FFFFFFF | absent when `0x04 & options` is 0. length of payload bytes
| 11  | data             | yes      | byte[]     | 0x00-0xFF ...   | absent when `0x04 & options` is 0. payload bytes
| 12  | signature        | yes      | byte[]     | 0x00-0xFF ...   | absent when `0x02 & options` is 0. signature bytes


----
#### How to encode data bytes?

```
raw --> compressed --> encrypted --> signed
```


----
#### How to decode data bytes?


```
checked --> decrypted --> decompressed --> raw
```
