package com.kodomo.yummy.util;

import com.kodomo.yummy.entity.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-06-03 20:48
 */
@Slf4j
@Component
public class AddressGetter {

    private List<Location> locations = null;

    private final List<String> notes = new ArrayList<>(Arrays.asList("楼下", "隔壁", "5号"));

    private String getRandomNote() {
        Random random = new Random();
        return notes.get((int) (random.nextDouble() * notes.size()));
    }

    public List<Location> getLocations() {
        if (locations == null) {
            try {
                getLocationsFromTencent();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (locations == null) {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>(locations);
    }

    private void getLocationsFromTencent() throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://apis.map.qq.com/ws/place/v1/suggestion";
        String bodyValTemplate = "?keyword=" + URLEncoder.encode("鼓楼", "utf-8")
                + "&region=" + URLEncoder.encode("南京", "utf-8")
                + "&key=HYVBZ-QI7C5-EBFIA-QJMEN-DAVT2-S7B4Q"
                + "&page_size=20";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url + bodyValTemplate, String.class);

        JsonParser jsonParser = new JacksonJsonParser();
        Map<String, Object> map = jsonParser.parseMap(responseEntity.getBody());
        System.out.println(map);

        int status = (int) map.get("status");
        if (!(status == 0)) {
            log.error((String) map.get("message"));
            return;
        }

        List<Map<String, Object>> jsonList = (List<Map<String, Object>>) map.get("data");
        if (jsonList != null) {
            locations = new ArrayList<>();
            for (Map<String, Object> m : jsonList) {
                locations.add(parseLocation(m));
            }
        }
    }

    private Location parseLocation(Map<String, Object> map) {
        Location location = new Location();
        location.setCity((String) map.get("city"));
        location.setBlockInfo((String) map.get("address"));
        location.setPointInfo((String) map.get("title"));
        Map<String, Object> latLng = (Map<String, Object>) map.get("location");
        location.setLat((Double) latLng.get("lat"));
        location.setLng((Double) latLng.get("lng"));
        location.setNote(getRandomNote());
        return location;
    }
}
