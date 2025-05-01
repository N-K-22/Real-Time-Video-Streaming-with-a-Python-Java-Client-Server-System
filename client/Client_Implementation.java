import java.io.IO;
import org.opencv.*;
import org.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;
import java.io.IOException;
import java.net.*;
import org.json.JSONObject;
import java.io.*;
//Apologies beforehand if the code looks very convoluted and messy, it has been a while since I have used Java

public class Client_Implementation{
    private ArrayList frame_storage;
    private String path_video = "";
    private VideoCapture video_capture;
    private  Mat video_frame; //https://opencv-java-tutorials.readthedocs.io/en/latest/03-first-javafx-application-with-opencv.html
    public Client_Implementation(){
        this.video_capture = new VideoCapture(path_video);
        if (!video_capture.isOpened()){
            System.out.println("Video file could not be loaded in");
            return;
        }
        this.video_frame = new Mat();
        is_finished = true;
    }

    private boolean is_finished;

    public boolean read_single_frame(){ //reading in the video frames one by one
        this.is_finished = this.video_capture.read(this.video_frame);
        if (is_finished){
            frame_storage.add(video_frame);
        }
        return is_finished;
    } 
    private void view_frame(Mat frame){
        HighGui.imshow("Frames to send over UDP",frame);
    }
    private void close_frames(){
        HighGui.destroyAllWindows();
    }
    public boolean frames_processed(){
        this.video_capture.release();
        return true;
    }

}
class Socket_Transmission_Frames extends Client_Implementation{
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
class LISTEN_IN implements Emitter.Listener{
    @Override
    public void call(Object... args){
        System.out.println("Connected");
    }

}
class DISCONNECTED_LISTENER implements Emitter.Listener{
    @Override
    public void call(Object... args){
        JSONObject why = (JSONObject) args[0];
        System.out.println("Disconnected due to " + why);
    }

}
class video_streaming extends Socket_Transmission_Frames {
    private Socket sock;
    private boolean connected;
    LISTEN_IN listen;
    DISCONNECTED_LISTENER disconnected_listener;
    MESSAGE message;
    public video_streaming(){
        this.connected = false;
    }
    public boolean is_connected(){
        return connected;
    }
    public void connect_socket(String url_to_connect){
        try{
            this.sock = IO.socket(url_to_connect);
            this.listen = new LISTEN_IN();
            disconnected_listener = new DISCONNECTED_LISTENER();
            sock.on(Socket.EVENT_CONNECT,listen);
            sock.on(Socket.EVENT_DISCONNECT,disconnected_listener);
            this.message = new MESSAGE();
            sock.on("feedback",message);
            sock.connect();
            this.connected = true;
            }catch (Exception e){
                this.connected = false;
                System.out.println("Failed to connect");
            }
    } 
    public void send_messaeg(JSONObject letter){
        sock.emit("feedback",letter);
    }
    public void disconnect(){
        if (this.sock != null){
            sock.disconnect();
        }
    }

}
class MESSAGE implements Emitter.Listener{
    @Override
    public void call(Objects... args){
        System.out.println("Received over socket: " + args)
        JSONObject message = (JSONObject) args;
        LocalTime current = LocalTime.now(); // https://sentry.io/answers/how-do-i-get-the-current-date-and-time-in-java/#:~:text=Summary,for%20a%20specific%20time%20zone.
        System.out.println("Received at "+ current + ": " + message);
    }
}