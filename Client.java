package MultiTransferFile;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
public class Client {
    private static DataInputStream dataInputStream = null;
    private static volatile boolean isPaused = false;
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 900)) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            System.out.println("Downloading file from the Server: ");

            // Start a new thread for download to allow for pausing and resuming
            Thread downloadThread = new Thread(() -> {
                try {
                    receiveFile("Test1.txt");
                    // Simulate pausing after 2 seconds
                    Thread.sleep(2000);
                    pauseDownload();
                 // Simulate resuming after 2 seconds
                    Thread.sleep(2000);
                    resumeDownload();
                    Thread.sleep(2000);
                    //receiveFile("[=-.docx");
                  //  Thread.sleep(1000);
                    System.out.println("Transfer Successful.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            downloadThread.start();
            // Wait for the download thread to finish
            downloadThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataInputStream != null)
                    dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void receiveFile(String fileName) throws Exception {
        int bytesRead;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong(); // read file size
        byte[] buffer = new byte[4 * 1024];

		while (size > 0 && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            // Check if the download is paused
            while (isPaused) {
                Thread.sleep(1000);
            }
            // Here we write the file using write method
            fileOutputStream.write(buffer, 0, bytesRead);
            size -= bytesRead; // read up to file size
        }
        // Here we received the file
        System.out.println("File " + fileName + " is Received");
        fileOutputStream.close();
    }
    private static void pauseDownload() {
        System.out.println("Download paused");
        isPaused = true;
    }
    private static void resumeDownload() {
        System.out.println("Resuming download");
        isPaused = false;
    }
}
