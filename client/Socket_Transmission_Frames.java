import java.io.IO;
import org.opencv.*;
import org.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;
import java.io.IOException;
import java.net.*;
import org.json.JSONObject;
import java.io.*;

public class Socket_Transmission_Frames extends Client_Implementation{
    private boolean connected;
    private DatagramSocket sock;
    private InetAddress server_address;
    private int port;

    public Socket_Transmission_Frames(String address. int port){
        try{
            this.sock = new DatagramSocket();
            this.server_address = InetAddress.getbyName(address);
            this.port = port;
        }
        catch(IOException e){
            System.out.println("Failed to connect client to server! Exception Message:"+ e.getMessage);
            connected = false;
        }
    }
    public DatagramPacket packet_creation(byte[] frame){
        DatagramPacket packet = new DatagramPack(frame, frame.length, this.server_address, this.port);
     //https://stackoverflow.com/questions/6137140/return-a-byte-array-from-a-java-method
     // https://docs.oracle.com/javase/8/docs/api/java/net/DatagramPacket.html
        return packet;
    }

    public boolean send_frame(byte[] frame){
        try{
            Datagram packet = packet_creation(frame);
            this.sock.send(packet);
            
        }catch(Exception e){
            System.err.println("Failed to send UDP packet");
            return false;
        }
        return true;
    }

    public boolean is_connect(){
        if (this.sock != null && !sock.isClosed()) connected = true;
        return true;
    }

    public void close_socket(){
        if (this.sock != null  || !(this.sock.isClosed())){
            this.sock.close();
        }
    }
}