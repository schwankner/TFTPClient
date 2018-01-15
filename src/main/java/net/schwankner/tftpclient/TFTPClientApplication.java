package net.schwankner.tftpclient;

import org.apache.commons.cli.*;

import java.net.UnknownHostException;

/**
 * Created by Alexander Schwankner on 13.01.18.
 */
public class TFTPClientApplication {
    public static void main(String[] args) throws ParseException, UnknownHostException {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("i", "inputFile", true, "path to input file");
        options.addOption("o", "outputFile", true, "path to output file");
        options.addOption("n", "protocol", true, "use tcp or udp as transport protocol. Default: udp");
        options.addOption("p", "port", true, "port for connection with remote host. Default: 69");
        options.addOption("t", "timeout", true, "timeout between sending and retries. Default: 10");
        options.addOption("r", "retries", true, "How many times tftpclient retries to send its messages. Default: 5");
        options.addOption("v", "verbose", false, "Verbose output for debuging");
        options.addOption("h", "help", false, "echos this help");

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                helpInformation(options);
                System.exit(0);
            }

            if (!line.hasOption("inputFile")) {
                System.out.println("input file has to be set");
                helpInformation(options);
                System.exit(1);
            }
            if (!line.hasOption("outputFile")) {
                System.out.println("output file has to be set");
                helpInformation(options);
                System.exit(1);
                //System.out.println(line.getOptionValue("writeFile"));
            }
            if (line.getArgs().length <= 0 && line.getOptions().length <= 0) {
                helpInformation(options);
                System.exit(1);
            }

            //check if at least one one path is a remote path
            if (line.getOptionValue("inputFile").contains("@")) {
                //split path in ip and local path
                String[] inputParts = line.getOptionValue("inputFile").split("@");
                //Call READ
                TFTPClient tftpClient = new TFTPClient(
                        inputParts[0],
                        Integer.parseInt(line.getOptionValue("port", "69")),
                        Integer.parseInt(line.getOptionValue("timeout", "10")) * 1000,
                        Integer.parseInt(line.getOptionValue("retries", "5"))
                );
                tftpClient.readFile(inputParts[1], line.getOptionValue("outputFile"));
                System.exit(0);

            } else if (line.getOptionValue("outputFile").contains("@")) {
                //split path in ip and local path
                String[] outputParts = line.getOptionValue("outputFile").split("@");
                //Call WRITE
                TFTPClient tftpClient = new TFTPClient(
                        outputParts[0],
                        Integer.parseInt(line.getOptionValue("port", "69")),
                        Integer.parseInt(line.getOptionValue("timeout", "10")) * 1000,
                        Integer.parseInt(line.getOptionValue("retries", "5"))
                );
                tftpClient.writeFile(outputParts[1], line.getOptionValue("inputFile"));

                System.exit(0);

            } else {
                System.out.println("Input or output path has to be remote e.g. 192.168.1.2@/home/user/file");
                System.exit(1);
            }


        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            System.exit(1);
        }

    }

    /**
     * automatically generate the help statement
     *
     * @param options
     */
    private static void helpInformation(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("tftpclient", options);
    }
}