import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.json.JSONObject;

public class javaWeather {

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws IOException {

        // global variables
        String ip;
        String zipCode;
        Double longitude = 0.00;
        Double latitude = 0.00;

        try {
            URL ipAddress = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(ipAddress.openStream()));

            ip = in.readLine();

            // get corresponding zip code for IP address from ipapi
            try {
                URL ipAPI = new URL("https://ipapi.co/" + ip + "/json");
                HttpURLConnection con = (HttpURLConnection) ipAPI.openConnection();
                System.out.println("Sending 'GET' request to URL : " + ipAPI);
                System.out.println("Response Code : " + con.getResponseCode());

                BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer ipResponse = new StringBuffer();

                String inputLine;
                while ((inputLine = input.readLine()) != null) {
                    ipResponse.append(inputLine);
                }

                // read JSON response
                JSONObject apiResponse = new JSONObject(ipResponse.toString());
                System.out.println("IP address: " + apiResponse.getString("ip"));

                zipCode = apiResponse.getString("postal");
                System.out.println("Zip Code: " + zipCode);
                System.out.println("Location: " + apiResponse.getString("city") + ", " + apiResponse.getString("region"));

                System.out.println(ipResponse);

                longitude = (Double) apiResponse.get("longitude");
                latitude = (Double) apiResponse.get("latitude");

                System.out.println("Coordinates: " + longitude + ", " + latitude);

                input.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

        // if you can't get the user IP, prompt for user zip code
        catch (Exception e) {
            Scanner input = new Scanner(System.in);
            System.out.print("Please input your 5 digit zip code: ");
            zipCode = input.next();
            System.out.println(zipCode);

        }

        try {

            URL weatherAPI = new URL("https://api.darksky.net/forecast/aee5400b392a39d03d8d839b4a34d4dc" + "/" + latitude + "," + longitude);
            HttpURLConnection connection = (HttpURLConnection) weatherAPI.openConnection();
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer responseBuffer = new StringBuffer();

            String inputLine;
            while ((inputLine = inputBuffer.readLine()) != null) {
                responseBuffer.append(inputLine);
            }

            // read JSON response
            JSONObject darkSkyResponse = new JSONObject(responseBuffer.toString());

            System.out.println(darkSkyResponse);

            inputBuffer.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }
}

// get ip address of computer running program

// get zip code from ip

// get weather from zip code
