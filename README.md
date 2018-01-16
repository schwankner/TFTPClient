# TFTPClient
Client for the implementation of the TFTP specification [RFC 1350](https://tools.ietf.org/html/rfc1350) in Java.

## Usage
| Parameter       | Description                                              | Default | Example                       |
| :--------------- |:--------------------------------------------------------| :-------:|------------------------------:|
| -i, --inputFile  | Path to input file                                      |         | testfile.txt                  |
| -o, --outputFile | Path to output file                                     |         | 192.168.1.2@output.txt |
| -p, --remotePort | RemotePort for connection with remote host.             | 69      | 2069 |
| -r, --retries    | How many times tftpclient retries to send its messages. | 5 sec.  | 5 |
| -t, --timeout    | Timeout between sending and retries.                    | 10 sec. | 10 |
| -v, --verbose    | Verbose output for debugging                            |         |  |
| -h, --help       | This help table.                                        |         |       |
#### Example
    java -jar TFTPClient-jar-with-dependencies.jar --inputFile=testfile.txt --outputFile=localhost@filepath\output.txt --retries=5 --timeout=10 -v
##Compilation
 - First, get the [TFTPLibrary](https://github.com/mrmoor/TFTPLibrary) and build it in its root directory with:
   
       mvn package
       
 - Then clone this repository and adjust the systemPath in the dependency net.schwankner.tftplibrary to your local path to the library's .jar file.
 - Now build this project with:
       
       mvn package
       
 - You will find the .jar file under ./target/TFTPClient-jar-with-dependencies.jar. You can copy and rename this file to whatever you want and put it wherever you want. Happy TFTP ;)
 
## Contribution and License Agreement

If you contribute code to this project, you are implicitly allowing your
code to be distributed under the respective license. You are also implicitly
verifying that all code is your original work or correctly attributed
with the source of its origin and licence.
 
## License
Modifications and samples are [GPL-3][LICENSE].

* Copyright (c) 2018, Jonas Hilke
* Copyright (c) 2018, Alexander Schwankner