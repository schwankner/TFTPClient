# TFTPClient
Client Implementierung der TFTP Spezifikation [RFC 1350](https://tools.ietf.org/html/rfc1350)
## Usage
    usage: tftpclient
     -i,--inputFile <arg>    path to input file
     -n,--protocol <arg>     use tcp or udp as transport protocol. Default: udp
     -o,--outputFile <arg>   path to output file
     -p,--remotePort <arg>         remotePort for connection with remote host. Default: 69
     -r,--retries <arg>      how many times tftpclient retries to send its messages. Default: 5
     -t,--timeout <arg>      timeout between sending and retries. Default: 10
     -v,--verbose            verbose output for debuging