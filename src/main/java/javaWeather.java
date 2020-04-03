import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import org.json.*;
import java.time.*;
import java.text.SimpleDateFormat;

public class javaWeather {

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws IOException {

        // global variables
        String ip;
        String zipCode;
        Double longitude = 0.00;
        Double latitude = 0.00;

        // get IP address
        try {
            URL ipAddress = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(ipAddress.openStream()));

            ip = in.readLine();

            // get corresponding zip code for IP address from ipApi
            try {
                URL ipAPI = new URL("https://ipapi.co/" + ip + "/json");
                HttpURLConnection con = (HttpURLConnection) ipAPI.openConnection();

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

            //printJsonObject(darkSkyResponse);

            printCurrentWeather(darkSkyResponse);

            printHourlyWeather(darkSkyResponse);

            inputBuffer.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public static void printCurrentWeather(JSONObject weatherReport) {
        ArrayList<String> currentWeather = new ArrayList<>();

        JSONObject current = weatherReport.getJSONObject("currently");

        // convert epoch time stamp to local time

        Long milliTime = Long.parseLong(current.get("time").toString()) * 1000;
        Date epochTime = new Date(milliTime);

        currentWeather.add(epochTime.toString());
        currentWeather.add("summary: " + current.get("summary").toString());
        currentWeather.add("wind speed: " + current.get("windGust").toString());
        currentWeather.add("temperature: " + current.get("temperature").toString());
        currentWeather.add("apparent temperature: " + current.get("apparentTemperature").toString());

        System.out.println(currentWeather);

    }

    public static void printHourlyWeather(JSONObject weatherReport) {
        String[][] hourWeather = new String[12][5];

        JSONArray hourlyWeather = weatherReport.getJSONObject("hourly").getJSONArray("data");

        // get weather reports for the next 12 hours
        for (int n = 0; n < 12; n++) {
            JSONObject hour = (JSONObject) hourlyWeather.get(n);

            Long milliTime = Long.parseLong(hour.get("time").toString()) * 1000;
            Date epochTime = new Date(milliTime);

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedDate = sdf.format(epochTime);

            hourWeather[n] = new String[] {

                    formattedDate,
                    hour.get("summary").toString(),
                    "Rain %: " + hour.get("precipProbability").toString(),
                    "feels like: " + hour.get("apparentTemperature").toString(),
                    "temperature: " + hour.get("temperature").toString()
            };

            System.out.println(Arrays.toString(hourWeather[n]));
        }

    }

    public static void printJsonObject(JSONObject jsonObj) {
        jsonObj.keySet().forEach(keyStr ->
        {
            Object keyvalue = jsonObj.get(keyStr);
            System.out.println("key: "+ keyStr + "        value: " + keyvalue);

            //for nested objects iteration if required
            //if (keyvalue instanceof JSONObject)
            //    printJsonObject((JSONObject)keyvalue);
        });
    }
}