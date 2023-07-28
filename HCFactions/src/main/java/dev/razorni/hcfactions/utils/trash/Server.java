package dev.razorni.hcfactions.utils.trash;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@UtilityClass
public class Server {

    public final String SERVER_VERSION =
            Bukkit.getServer()
                    .getClass().getPackage()
                    .getName().split("\\.")[3]
                    .substring(1);

    public final int SERVER_VERSION_INT = Integer.parseInt(
            SERVER_VERSION
                    .replace("1_", "")
                    .replaceAll("_R\\d", ""));

    public String getIP() {
        String ipAddress;

        try {
            URL url = new URL("http://checkip.amazonaws.com/%22");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = in.readLine()) != null) {
                builder.append(line);
            }

            ipAddress = builder.toString();
        } catch (UnknownHostException ex) {
            ipAddress = "NONE";
            ChatUtil.log("[PandaLicense] Problem on the page with the IPS.");
        } catch (IOException ex) {
            ipAddress = "NONE";
            ChatUtil.log("[PandaLicense] Error in check your host IP.");
        }
        return ipAddress;
    }

    public String getDate(String dateFormat, String timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return simpleDateFormat.format(new Date());
    }

    public String getHour(String hourFormat, String timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(hourFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return simpleDateFormat.format(new Date());
    }
}
