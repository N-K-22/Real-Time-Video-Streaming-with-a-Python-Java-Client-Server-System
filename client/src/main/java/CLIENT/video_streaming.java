package CLIENT;
import org.json.JSONObject;
import io.socket.client.IO;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import io.socket.emitter.Emitter;
import io.socket.client.Socket;

class MESSAGE implements Emitter.Listener{
    @Override
    public void call(Object... args){
        System.out.println("Received over socket: " + args);
        JSONObject message = (JSONObject) args[0];
        LocalTime current = LocalTime.now(); // https://sentry.io/answers/how-do-i-get-the-current-date-and-time-in-java/#:~:text=Summary,for%20a%20specific%20time%20zone.
        System.out.println("Message Acknowledgement Received at "+ current + ": " + message);
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
public class video_streaming extends Socket_Transmission_Frames {
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
            this.connected = true;
            this.listen = new LISTEN_IN();
            disconnected_listener = new DISCONNECTED_LISTENER();
            sock.on(Socket.EVENT_CONNECT,listen);
            sock.on(Socket.EVENT_DISCONNECT,disconnected_listener);
            this.message = new MESSAGE();
            sock.on("feedback",message);
            sock.on(Socket.EVENT_CONNECT_ERROR, args -> {
                System.out.println("Connection error: " + Arrays.toString(args));
            });
            sock.connect();
            sock.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("Connected to server");
                //sock.emit("feedback", message); // Only emit after confirmed connect
            });
            System.out.println("Connected to server compltetey");
        }catch (Exception e){
            this.connected = false;
            System.out.println("Failed to connect");
        }
    }
    public void send_message(JSONObject letter){
        sock.emit("feedback",letter);
    }
    public void disconnect(){
        try{
            if (this.sock != null){
                this.sock.close();
                connected = false;
            }
        } catch (Exception e) {
            System.out.println("Connection status: " + connected);
            //might cause issues
        }
    }
}