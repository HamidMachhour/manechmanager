package com.manech.app.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.manech.app.helper.MyColor;
import com.manech.app.helper.StringValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
        indices = {@Index("userId")})

public class ManechNode {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;

    // GENERAL SETTINGS:
    private String name;
    private String description;
    private String httpLogin;
    private String httpPassword;
    private int minPixel;
    private int maxPixel;
    private int minIndex;
    private int maxIndex;
    private String defaultIP;

    // ACCESS POINT SETTINGS:
    private String apName;
    private String apKey;
    private String apIP;
    private String apMask;
    private String apGateway;
    @Ignore
    private int nbStations;

    //WIFI SETTINGS:
    private String wifiName;
    private String wifiKey;
    private String wifiIP;
    private String wifiMask;
    private String wifiGateway;

    @Ignore
    private JSONArray availableWifi;
    @Ignore
    private boolean highlighted = false;
    @Ignore
    private int state = OFFLINE_STATE;
    @Ignore
    public static final int ONLINE_STATE = 1;
    @Ignore
    public static final int OFFLINE_STATE = 2;

    public ManechNode(long userId, String name, String description, String httpLogin,
                      String httpPassword, int minPixel, int maxPixel,
                      String apName, String apKey, String apIP, String apMask, String apGateway,
                      String wifiName, String wifiKey, String wifiIP, String wifiMask, String wifiGateway) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.httpLogin = httpLogin;
        this.httpPassword = httpPassword;

        this.apName = apName;
        this.apKey = apKey;
        this.apIP = apIP;
        this.defaultIP = apIP;
        this.apMask = apMask;
        this.apGateway = apGateway;
        this.wifiName = wifiName;
        this.wifiKey = wifiKey;
        this.wifiIP = wifiIP;
        this.wifiMask = wifiMask;
        this.wifiGateway = wifiGateway;

        minPixel = (minPixel < 0) ? 0 : minPixel;
        maxPixel = (maxPixel < 0) ? 0 : maxPixel;

        if (minPixel > maxPixel) {
            this.minPixel = maxPixel;
            this.maxPixel = minPixel;
        } else {
            this.minPixel = minPixel;
            this.maxPixel = maxPixel;
        }

        this.minIndex = this.minPixel;
        this.maxIndex = this.maxPixel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHttpLogin() {
        return httpLogin;
    }

    public void setHttpLogin(String httpLogin) {
        this.httpLogin = httpLogin;
    }

    public String getHttpPassword() {
        return httpPassword;
    }

    public void setHttpPassword(String httpPassword) {
        this.httpPassword = httpPassword;
    }

    public int getMinPixel() {
        return minPixel;
    }



    public int getMaxPixel() {
        return maxPixel;
    }


    public int getMinIndex() {
        return minIndex;
    }
    public void setPixels(int minPixel, int maxPixel){
        if(minPixel<0 || maxPixel<0) return;
        if(minPixel>maxPixel ){
            int t=minPixel;
            minPixel=maxPixel;
            maxPixel=t;
        }
        this.minPixel=minPixel;
        this.maxPixel=maxPixel;
        validateIndexes();
    }
    public void setIndexes(int minIndex, int maxIndex) {
        if (minIndex > maxIndex) {
            int t = minIndex;
            minIndex = maxIndex;
            maxIndex = t;
        }
        this.minIndex=minIndex;
        this.maxIndex=maxIndex;
        validateIndexes();
    }

    public void setMinPixel(int minPixel) {
        this.minPixel = minPixel;
    }

    public void setMaxPixel(int maxPixel) {
        this.maxPixel = maxPixel;
    }

    public void setMinIndex(int minIndex) {
        this.minIndex = minIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }
    private void validateIndexes() {
        minIndex = (maxIndex - minIndex > maxPixel - minPixel) ? maxIndex - (maxPixel - minPixel): minIndex;
    }

    public int getMaxIndex() {
        return maxIndex;
    }


    public String getApName() {
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }

    public String getApKey() {
        return apKey;
    }

    public void setApKey(String apKey) {
        this.apKey = apKey;
    }

    public String getApIP() {
        return apIP;
    }

    public void setApIP(String apIP) {
        this.apIP = apIP;
    }

    public String getApMask() {
        return apMask;
    }

    public void setApMask(String apMask) {
        this.apMask = apMask;
    }

    public String getApGateway() {
        return apGateway;
    }

    public void setApGateway(String apGateway) {
        this.apGateway = apGateway;
    }

    public int getNbStations() {
        return nbStations;
    }

    public void setNbStations(int nbStations) {
        this.nbStations = nbStations;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiKey() {
        return wifiKey;
    }

    public void setWifiKey(String wifiKey) {
        this.wifiKey = wifiKey;
    }

    public String getWifiIP() {
        return wifiIP;
    }

    public void setWifiIP(String wifiIP) {
        this.wifiIP = wifiIP;
    }

    public String getWifiMask() {
        return wifiMask;
    }

    public void setWifiMask(String wifiMask) {
        this.wifiMask = wifiMask;
    }

    public String getWifiGateway() {
        return wifiGateway;
    }

    public void setWifiGateway(String wifiGateway) {
        this.wifiGateway = wifiGateway;
    }

    public boolean isAccessPoint() {
        return apIP.equals(defaultIP);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDefaultIP() {
        return defaultIP;
    }

    public void setDefaultIP(String defaultIP) {
        this.defaultIP = defaultIP;
    }

    public JSONArray getAvailableWifi() {
        return availableWifi;
    }

    public void setAvailableWifi(JSONArray availableWifi) {
        this.availableWifi = availableWifi;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public ManechNode clone() {
        final ManechNode manechNode = new ManechNode(userId, name, description, httpLogin, httpPassword, minPixel, maxPixel,
                apName, apKey, apIP, apMask, apGateway,
                wifiName, wifiKey, wifiIP, wifiMask, wifiGateway);
        manechNode.setDefaultIP(this.defaultIP);
        return manechNode;
    }

    public JSONArray preparePixels(int a, int b, int[] indexes, MyColor c) {
        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject animation = new JSONObject();
            boolean bool = false;
            if (a >= minIndex && b <= maxIndex) {
                bool = true;
                animation.put("min_range", a - minIndex + minPixel);
                animation.put("max_range", b - minIndex + minPixel);
            }
            animation.put("color", c.toJSONObject());

            if (indexes != null && indexes.length > 0) {
                String idx = "[";
                int i;
                for (i = 0; i < indexes.length - 1; i++) {
                    if (indexes[i] < minIndex || indexes[i] > maxIndex ||
                            (bool && indexes[i] >= a && indexes[i] <= b)) continue;
                    idx += (indexes[i] - minIndex + minPixel) + ",";
                }
                idx += indexes[i] + "]";
                animation.put("indexes", idx);
            }
            jsonArray.put(animation);

            return jsonArray;

        } catch (Exception e) {
        }
        return null;
    }

    public JSONObject prepareConfiguration() {
        StringValidation sv = new StringValidation();
        try {
            JSONObject jsonObject = new JSONObject();
            // general settings
            jsonObject.put("hostname", sv.emptyIfNull(name));
            jsonObject.put("www_username", sv.emptyIfNull(httpLogin));
            jsonObject.put("www_password", sv.emptyIfNull(httpPassword));
            jsonObject.put("min_leds", minPixel);
            jsonObject.put("max_leds", maxPixel);

            //access point settings
            jsonObject.put("ap_ip", sv.emptyIfNull(apIP));
            jsonObject.put("ap_mask", sv.emptyIfNull(apMask));
            jsonObject.put("ap_gw", sv.emptyIfNull(apGateway));
            jsonObject.put("ap_ssid", sv.emptyIfNull(apName));
            jsonObject.put("ap_password", sv.emptyIfNull(apKey));

            // wifi settings
            jsonObject.put("wifi_ssid", sv.emptyIfNull(wifiName));
            jsonObject.put("wifi_password", sv.emptyIfNull(wifiKey));
            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void backupConfiguration(JSONObject response) {
        try {
            // general settings
            setState(ManechNode.ONLINE_STATE);
            if (response.has("hostname"))
                setName(response.getString("hostname"));
            if (response.has("www_username"))
                setHttpLogin(response.getString("www_username"));
            if (response.has("www_password"))
                setHttpPassword(response.getString("www_password"));
            if (response.has("min_leds") && response.has("max_leds"))
                setPixels(response.getInt("min_leds"),response.getInt("max_leds"));

            //access point settings
            if (response.has("ap_ip"))
                setApIP(response.getString("ap_ip"));
            if (response.has("ap_mask"))
                setApMask(response.getString("ap_mask"));
            if (response.has("ap_gw"))
                setApGateway(response.getString("ap_gw"));
            if (response.has("ap_ssid"))
                setApName(response.getString("ap_ssid"));
            if (response.has("ap_password"))
                setApKey(response.getString("ap_password"));
            if (response.has("nb_stations"))
                setNbStations(response.getInt("nb_stations"));
            // wifi settings
            if (response.has("wifi_ssid"))
                setWifiName(response.getString("wifi_ssid"));
            if (response.has("wifi_password"))
                setWifiKey(response.getString("wifi_password"));
            if (response.has("available_wifi")) {
                JSONArray availableWifi = response.getJSONArray("available_wifi");
                setAvailableWifi(availableWifi);

            }
            if (response.has("current_status")) {
                JSONObject current_status = response.getJSONObject("current_status");

                if (current_status.has("wifi_ip"))
                    setWifiIP(current_status.getString("wifi_ip"));
                if (current_status.has("wifi_gw"))
                    setWifiGateway(current_status.getString("wifi_gw"));
                if (current_status.has("wifi_mask"))
                    setWifiMask(current_status.getString("wifi_mask"));
            }
        } catch (Exception e) {
        }
    }
}
