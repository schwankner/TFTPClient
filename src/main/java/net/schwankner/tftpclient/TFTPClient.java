package net.schwankner.tftpclient;

import net.schwankner.tftplibrary.FileSystem;
import net.schwankner.tftplibrary.Messages.AcknowledgementMessage;
import net.schwankner.tftplibrary.Messages.DataMessage;
import net.schwankner.tftplibrary.Messages.ReadMessage;
import net.schwankner.tftplibrary.Messages.WriteMessage;
import net.schwankner.tftplibrary.Network;
import net.schwankner.tftplibrary.ReceiveOperation;
import net.schwankner.tftplibrary.SendOperation;

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
    private int remotePort;
    private boolean verbose;

    public TFTPClient(String remoteHost, int remotePort, int timeout, int retries, boolean verbose) {
        try {
            this.verbose = verbose;
            this.remoteHost = InetAddress.getByName(remoteHost);
            this.remotePort = remotePort;
            this.network = new Network(remotePort, timeout, retries);
            this.network.connect(false);
        } catch (UnknownHostException e) {
            System.out.println("Unknown remote host: " + remoteHost);
            System.exit(1);
        }

    }

    public void writeFile(String remoteFile, String localFile) {

        System.out.println("Write file " + localFile + " to remote server " + remoteHost);

        //read file from filesystem
        byte[] inputFile = FileSystem.readFileToBlob(localFile);

        //Create data message storage
        SendOperation sendOperation = new SendOperation();

        //Fill data message storage with data from input file
        sendOperation.createMessageListFromBin(inputFile);

        //Create TFTP write request packet
        WriteMessage writeMessage = new WriteMessage(remoteFile);

        //Send TFTP write request packet
        try {
            verboseOutput("Send WRQ for local file " + localFile + " to remote file " + remoteFile + " to " + remoteHost);
            network.sendPacket(writeMessage.buildBlob(), remoteHost, true);
        } catch (Exception e) {
            System.err.println("No ACK received within timeout and max retries reached, exiting!");
            //Close socket connection
            network.close();
            System.exit(1);
        }

        //Send data packets via TFTP
        for (int i = 0; i < sendOperation.getMessageCollection().size(); i++) {
            try {
                verboseOutput("Send DATA #" + (i + 1));
                network.sendPacket(sendOperation.getMessageCollection().get(i).buildBlob(), remoteHost, true);
                verboseOutput("Got ACK #" + (i + 1));
            } catch (Exception e) {
                System.err.println("No ACK received within timeout and max retries reached, exiting!");
            }

        }

        System.out.println("File " + localFile + " send with " + sendOperation.getDataSize() + " bytes");
        //Close socket connection
        network.close();

    }

    public void readFile(String localFile, String remoteFile) {
        System.out.println("Read file " + remoteFile + " from remote server " + remoteHost);

        //Create TFT read request packet
        ReadMessage readMessage = new ReadMessage(remoteFile);

        //Send TFTP read request packet
        try {
            verboseOutput("Send RRQ for file " + remoteFile + " to " + remoteHost);
            network.sendPacket(readMessage.buildBlob(), remoteHost, false);
        } catch (Exception e) {
            //no real error handling here because if no Ack is awaited there can be no max retries or timeout
        }

        //Create data message storage
        ReceiveOperation receiveOperation = new ReceiveOperation(remoteHost, remotePort, localFile);

        //Receive Packets and write them to file
        boolean receivedLastDataPackage = false;
        while (!receivedLastDataPackage) {
            try {
                DatagramPacket packet = network.receivePacket();
                DataMessage dataMessage = new DataMessage(packet.getData());
                verboseOutput("Got DATA #" + dataMessage.getPacketNumber());
                receivedLastDataPackage = receiveOperation.addDatapackage(dataMessage);
                network.sendPacket(new AcknowledgementMessage(dataMessage.getPacketNumber()).buildBlob(), packet.getAddress(), packet.getPort(), false);
                verboseOutput("Send ACK #" + dataMessage.getPacketNumber());

            } catch (TimeoutException e) {
                System.err.println("No data received within timeout period");
                break;

            } catch (Exception e) {
                System.err.println(e);
                break;
            }
        }

        //Close socket connection
        network.close();
    }

    private void verboseOutput(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }
}
