package org.example.groworders.domain.weather.service;

import org.example.groworders.domain.weather.model.dto.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {

    @Value("${api.weather.apikey}")
    private static final String API_KEY = "qkWUrFW5RkSFlKxVudZE_g";  // 실제 키로 교체
    private static final String API_URL = "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php";
    private static final String DEFAULT_TM = getCurrentDate() + "0900";
    private static final String DEFAULT_STN = "108";

    public WeatherDto.WeatherData fetchWeatherData() {
        try {
            String urlStr = API_URL + "?tm=" + DEFAULT_TM + "&stn=" + DEFAULT_STN + "&authKey=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.trim().isEmpty()) continue;

                    String[] tokens = line.trim().split("\\s+");

                    String tm = tokens[0];
                    String stn = tokens[1];
                    String ws = tokens[3];     // 풍속
                    String ta = tokens[11];    // 기온
                    String hm = tokens[13];    // 습도
                    String rn = tokens[16];    // 강수량
                    String si = tokens[34];    // 일사량

                    return new WeatherDto.WeatherData(tm, stn, ws, ta, hm, rn, si);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}