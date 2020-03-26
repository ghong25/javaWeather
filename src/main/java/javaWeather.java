import java.io.*;
import java.net.*;

public class javaWeather {

    public static void main(String[] args) {

        try {
            URL ipAddress = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(ipAddress.openStream()));

            String ip = in.readLine();
            System.out.println(ip);


        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}

// get ip address of computer running program

// get zip code from ip

// get weather from zip code
