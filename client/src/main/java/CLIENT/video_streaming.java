package CLIENT;
import org.json.JSONObject;
import io.socket.client.IO;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import io.socket.emitter.Emitter;
import io.socket.client.Socket;

class LISTEN_IN implements Emitter.Listener{ //what happens when the SocketIO connection forms
    @Override
    public void call(Object... args){
        System.out.println("Connected"); //SocketIO connection is lost
    }
}
class DISCONNECTED_LISTENER implements Emitter.Listener{
    @Override
    public void call(Object... args){
        String why = args[0].toString();
        System.out.println("Disconnected due to " + why);
    }

}
public class video_streaming extends Socket_Transmission_Frames {
    private Socket sock;
    private boolean connected;
    LISTEN_IN listen;
    DISCONNECTED_LISTENER disconnected_listener;
    public video_streaming(){
        this.connected = false;
    }
    public boolean is_connected(){
        return connected;
    }
    public void connect_socket(String url_to_connect){
        try{ //socket connection and creation 
            this.sock = IO.socket(url_to_connect);
            this.connected = true;
            this.listen = new LISTEN_IN();
            disconnected_listener = new DISCONNECTED_LISTENER();
            sock.on(Socket.EVENT_CONNECT,listen);
            sock.on(Socket.EVENT_DISCONNECT,disconnected_listener);
            sock.on("feedback", new Emitter.Listener() { //removed separate class from original
                @Override
                public void call(Object... args) {
                    System.out.println("--------------");
                    //System.out.println("Received over socket: " + args);
                    JSONObject message = (JSONObject) args[0];
                    LocalTime current = LocalTime.now(); // https://sentry.io/answers/how-do-i-get-the-current-date-and-time-in-java/#:~:text=Summary,for%20a%20specific%20time%20zone.
                    System.out.println("Message Acknowledgement Received at "+ current + ": " + message);
                    System.out.println("--------------");
                }
            });
            sock.on(Socket.EVENT_CONNECT_ERROR, args -> {
                System.out.println("Connection error: " + Arrays.toString(args));
            });
            sock.connect();
            sock.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("Connected to server");
            });
            System.out.println("Connected to server compltetey");
        }catch (Exception e){
            this.connected = false;
            System.out.println("Failed to connect");
        }
    }
    public void send_message(JSONObject letter){ //in case of debugging to see whether things were actually getting sent
        sock.emit("feedback",letter);
    }
    public void disconnect(){ //disconnect soncket for clean shutdown
        try{
            if (this.sock != null){
                this.sock.close();
                connected = false;
            }
        } catch (Exception e) {
            System.out.println("Connection status: " + connected);
        }
    }
}