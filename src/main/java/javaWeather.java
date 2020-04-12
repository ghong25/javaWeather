import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import org.json.*;
import java.text.SimpleDateFormat;

public class javaWeather {

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws IOException {

        // global variables
        String ip;
        Double longitude = 0.00;
        Double latitude = 0.00;

        String weatherAPIKey = "aee5400b392a39d03d8d839b4a34d4dc";

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

                System.out.println("Location: " + apiResponse.getString("city") + ", " + apiResponse.getString("region"));


                longitude = (Double) apiResponse.get("longitude");
                latitude = (Double) apiResponse.get("latitude");

                //System.out.println("Coordinates: " + longitude + ", " + latitude);

                input.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

        catch (Exception e) {
            System.out.print(e);
        }

        try {

            URL weatherAPI = new URL("https://api.darksky.net/forecast/" + weatherAPIKey + "/" + latitude + "," + longitude);
            HttpURLConnection connection = (HttpURLConnection) weatherAPI.openConnection();
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer responseBuffer = new StringBuffer();

            String inputLine;
            while ((inputLine = inputBuffer.readLine()) != null) {
                responseBuffer.append(inputLine);
            }

            // read JSON response
            JSONObject darkSkyResponse = new JSONObject(responseBuffer.toString());

            inputBuffer.close();


            while (true) {

                Scanner input = new Scanner(System.in);
                System.out.println("Enter 'C' for the current weather, 'H' for an hourly report, 'W' for a weekly report, and 'E' to exit the program: ");
                String userInput = input.next();

                String[] options = {"c", "h", "w"};

                if (Arrays.asList(options).contains(userInput.toLowerCase())) {
                    if (userInput.toLowerCase().equals("c")) {
                        printCurrentWeather(darkSkyResponse);
                    } else if (userInput.toLowerCase().equals("h")) {
                        printHourlyWeather(darkSkyResponse);
                    } else if (userInput.toLowerCase().equals("w")) {
                        printWeeklyWeather(darkSkyResponse);
                    }
                }
                if (userInput.toLowerCase().equals("e")) {
                    break;
                }
            }

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

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH);
        String formattedDate = sdf.format(epochTime);

        currentWeather.add(formattedDate);
        currentWeather.add("summary: " + current.get("summary").toString());
        currentWeather.add("wind speed: " + current.get("windGust").toString());
        currentWeather.add("temperature: " + current.get("temperature").toString());
        currentWeather.add("feels like: " + current.get("apparentTemperature").toString());

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

    public static void printWeeklyWeather(JSONObject weatherReport) {

        JSONObject dailyWeather = weatherReport.getJSONObject("daily");
        JSONArray weekWeather = dailyWeather.getJSONArray("data");


        // get weather reports for the next 12 hours
        for (int n = 0; n < 7; n++) {
            JSONObject day = (JSONObject) weekWeather.get(n);

            Date epochTime = new Date(Long.parseLong(day.get("time").toString()) * 1000);

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            String formattedDate = sdf.format(epochTime);

            String[] dayWeather = new String[] {
                    formattedDate,
                    day.get("summary").toString(),
                    "Rain %: " + day.get("precipProbability").toString(),
                    "min temp: " + day.get("temperatureLow").toString(),
                    "max temp: " + day.get("temperatureMax").toString()
            };

            System.out.println(Arrays.toString(dayWeather));
        }
    }

}