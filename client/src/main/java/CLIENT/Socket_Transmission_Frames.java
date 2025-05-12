package CLIENT;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;


public class Socket_Transmission_Frames extends Client_Implementation{
    private boolean connected;
    private DatagramSocket sock;
    private InetAddress server_address;
    private int port;

    public Socket_Transmission_Frames(String address, int port){
        try{
            this.server_address = InetAddress.getByName(address); //https://docs.oracle.com/javase/8/docs/api/java/net/InetAddress.html
            System.out.println("Server address: " + this.server_address);
            this.port = port;
            System.out.println("Server port: " + this.port);
            this.sock = new DatagramSocket();
            if (this.sock == null || this.sock.isClosed()) {
                System.out.println("Socket is not initialized or is closed.");
            }
            System.out.println("Socket connected");

        }
        catch(IOException e){
            System.out.println("Failed to connect client to server! Exception Message:"+ e.getMessage());
            connected = false;
        }
    }

    public Socket_Transmission_Frames() {
    }

    public DatagramPacket packet_creation(byte[] frame){
        DatagramPacket temp_packet = new DatagramPacket(frame, frame.length, this.server_address, this.port);
        //https://stackoverflow.com/questions/6137140/return-a-byte-array-from-a-java-method
        // https://docs.oracle.com/javase/8/docs/api/java/net/DatagramPacket.html
        return temp_packet;
    }

    public boolean send_frame(byte[] frame){
        try{
            //find the number of transmissions that need to occur:
            int number_of_transmissions_needed = (int)Math.ceil((double)frame.length/(double)65507); //https://stackoverflow.com/questions/3396813/message-too-long-for-udp-socket-after-setting-sendbuffersize
            byte[] frame_bytes;
            int min = 0;
            int max = 0;
            for(int i = 0; i<number_of_transmissions_needed-1; i++){ //sending components of frames ocnsecutively due to size constraints
                min = i*65507;
                max = min+65507;
                frame_bytes = Arrays.copyOfRange(frame, min, max);
                DatagramPacket packet = packet_creation(frame_bytes);
                this.sock.send(packet);
                Thread.sleep(100);

            }
            //sending last packet
            byte[] last_frame = Arrays.copyOfRange(frame,max , frame.length);
            DatagramPacket packet = packet_creation(last_frame);
            this.sock.send(packet);
            //System.out.println("Transmissions sent for Full Frame"); //--> comment out for debugging

        }catch(Exception e){
            System.err.println("Failed to send UDP packet. " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
    public boolean is_connect(){ //boolean check 
        if (this.sock != null && !sock.isClosed()) connected = true;
        return true;
    }

    public void close_socket(){ //used for clean shutdown

        try{
            if (this.sock != null  && !(this.sock.isClosed())){
                this.sock.close();
            }
        }catch (Exception e){
            System.out.println("Socket Closed Already?");
        }
    }
}