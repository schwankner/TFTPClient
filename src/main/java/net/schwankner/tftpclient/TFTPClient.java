package net.schwankner.tftpclient;

import net.schwankner.tftplibrary.*;
import net.schwankner.tftplibrary.Messages.DataMessage;
import net.schwankner.tftplibrary.Messages.WriteMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Alexander Schwankner on 13.01.18.
 */
public class TFTPClient {
    private Network network;
    private InetAddress remoteHost;

    public TFTPClient(String remoteHost, int port, int timeout, int retries){
        try {
            this.remoteHost=InetAddress.getByName(remoteHost);
            this.network = new Network(port,timeout,retries);
            this.network.connect(false);
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

        //Fill data message storage with data from input file
        messageList.createMessageListFromBin(inputFile);

        //Create TFTP write request packet
        WriteMessage writeMessage = new WriteMessage(remoteFile);

        //Send TFTP write request packet
        network.sendPacket(writeMessage.buildBlob(),remoteHost,true);

        //Send date packets via TFTP
        for (DataMessage dataMessage: messageList.getMessageCollection()) {
            network.sendPacket(dataMessage.buildBlob(),remoteHost,true);
        }

        //Close socket connection
        network.close();

    }
}
