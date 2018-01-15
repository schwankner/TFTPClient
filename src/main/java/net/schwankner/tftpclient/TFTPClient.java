package net.schwankner.tftpclient;

import net.schwankner.tftplibrary.FileSystem;
import net.schwankner.tftplibrary.Messages.DataMessage;
import net.schwankner.tftplibrary.Messages.ReadMessage;
import net.schwankner.tftplibrary.Messages.WriteMessage;
import net.schwankner.tftplibrary.Network;
import net.schwankner.tftplibrary.SendOperation;
import net.schwankner.tftplibrary.ReceiveOperation;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Alexander Schwankner on 13.01.18.
 */
public class TFTPClient {
    private Network network;
    private InetAddress remoteHost;

    public TFTPClient(String remoteHost, int port, int timeout, int retries) {
        try {
            this.remoteHost = InetAddress.getByName(remoteHost);
            this.network = new Network(port, timeout, retries);
            this.network.connect(false);
        } catch (UnknownHostException e) {
            System.out.println("Unknown remote host: " + remoteHost);
            System.exit(1);
        }

    }

    public void writeFile(String remoteFile, String localFile) {

        //read file from filesystem
        byte[] inputFile = FileSystem.readFileToBlob(localFile);

        //Create data message storage
        SendOperation sendOperation = new SendOperation();

        //Fill data message storage with data from input file
        sendOperation.createMessageListFromBin(inputFile);

        //Create TFTP write request packet
        WriteMessage writeMessage = new WriteMessage(remoteFile);

        //Send TFTP write request packet
        network.sendPacket(writeMessage.buildBlob(), remoteHost, true);

        //Send date packets via TFTP
        for (DataMessage dataMessage : sendOperation.getMessageCollection()) {
            network.sendPacket(dataMessage.buildBlob(),remoteHost,true);
        }

        //Close socket connection
        network.close();

    }

    public void readFile(String localFile, String remoteFile) {

        //Create TFT read request packet
        ReadMessage readMessage = new ReadMessage(remoteFile);

        //Send TFTP read request packet
        network.sendPacket(readMessage.buildBlob(), remoteHost, false);

        //
        ReceiveOperation receiveOperation = new ReceiveOperation(localFile);

        boolean receivedLastDataPackage = false;
        while (!receivedLastDataPackage) {
            try {
                DatagramPacket packet = network.receivePacket();
                DataMessage dataMessage = new DataMessage(packet.getData());
                receivedLastDataPackage = receiveOperation.addDatapackage(dataMessage);
            } catch (TimeoutException e) {

            } catch (Exception e){

            }
        }

        //Close socket connection
        network.close();
    }
}
