package com.test.PP_Machines_storage;

import com.jcraft.jsch.*;

import java.util.Vector;

public class annotation {
    public static void main(String[] args) {
        String host = "pp6.humanbrain.in";
        String user = "appUser";
        String password = "Brain@123";
        String remoteDir = "/lustre/data/store10PB/repos1/iitlab/humanbrain/analytics/29/appData/atlasEditor";

        Session session = null;
        ChannelSftp sftpChannel = null;

        try {
            // Step 1: Create a JSch session
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            // Avoid host key checking
            session.setConfig("StrictHostKeyChecking", "no");

            System.out.println("Connecting to " + host);
            session.connect();
            System.out.println("Connected!");

            // Step 2: Open SFTP channel
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            // Step 3: Count and print files containing "FlatTree" in their name
            int totalFileCount = countFilesInDirectory(sftpChannel, remoteDir);

            // Print the total file count for files containing "FlatTree"
            System.out.println("\nTotal files containing 'FlatTree' in their name: " + totalFileCount);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    /**
     * Recursively counts and prints all files containing "FlatTree" in their name in the given directory and its subdirectories.
     */
    private static int countFilesInDirectory(ChannelSftp sftpChannel, String path) {
        int fileCount = 0;
        try {
            Vector<ChannelSftp.LsEntry> fileList = sftpChannel.ls(path);

            for (ChannelSftp.LsEntry entry : fileList) {
                // Check if it's a directory
                if (entry.getAttrs().isDir() && !entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                    String subFolderPath = path + "/" + entry.getFilename();
                    // Print folder path with space before it
                    System.out.println("\n" + subFolderPath + "/");
                    // Recursively count files in subdirectories
                    fileCount += countFilesInDirectory(sftpChannel, subFolderPath);
                } else if (entry.getAttrs().isReg()) {
                    // Check if the filename contains "FlatTree"
                    if (entry.getFilename().contains("FlatTree")) {
                        // Print the file path with space before it
                        System.out.println(path + "/" + entry.getFilename());
                        fileCount++; // Increment count if "FlatTree" is in the filename
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error accessing: " + path);
        }
        return fileCount;
    }
}
