package net.schwankner.tftpclient;

import net.schwankner.tftplibrary.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Alexander Schwankner on 13.01.18.
 */
public class TFTPClient {
    private Network network;

    public TFTPClient(String remoteHost, int port, int timeout, int retries){
        try {
            this.network = new Network(InetAddress.getByName(remoteHost),port,timeout,retries);
            this.network.connect();
        } catch (UnknownHostException e){
            System.out.println("Unknown remote host: "+remoteHost);
            System.exit(1);
        }

    }

    public void writeFile(String remoteFile, String localFile) {
        //read file from filesystem
        byte[] inputFile = FileSystem.readFileToBin(localFile);

        //Create data message storage
        MessageList messageList = new MessageList();

        //Fill date message storage with data from input file
        messageList.createMessageListFromBin(inputFile);

        //Create TFTP Request Packet
        WriteRequest writeRequest = new WriteRequest(remoteFile);

        //Send TFTP Request Packet
        network.sendMessage(writeRequest.buildMessage(),false);

        //Send date packets via TFTP
        for (DataMessage dataMessage: messageList.getMessageCollection()) {
            network.sendMessage(dataMessage.buildMessage(),false);
        }

    }
}
