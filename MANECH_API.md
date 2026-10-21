# ManechNode HTTP API Specification

The ManechNode is controlled via a REST-like HTTP API using Basic Authentication.

## Authentication
All requests must include an `Authorization` header using Basic Auth:
`Authorization: Basic <base64(username:password)>`

## Endpoints

### 1. Get Configuration
Retrieves the current settings and status of the node.

- **URL:** `http://<node_ip>/get`
- **Method:** `POST`
- **Body:** `{}` (Empty JSON object)
- **Response:** A JSON object containing:
    - `hostname`, `www_username`, `www_password`, `min_leds`, `max_leds`
    - `ap_ip`, `ap_mask`, `ap_gw`, `ap_ssid`, `ap_password`
    - `wifi_ssid`, `wifi_password`
    - `nb_stations` (number of connected stations)
    - `available_wifi` (list of scanned networks)
    - `current_status` (contains `wifi_ip`, `wifi_gw`, `wifi_mask`)

### 2. Set Configuration
Updates the node's identity and network settings.

- **URL:** `http://<node_ip>/set`
- **Method:** `POST`
- **Body:** 
```json
{
  "hostname": "NodeName",
  "www_username": "admin",
  "www_password": "password",
  "min_leds": 0,
  "max_leds": 63,
  "ap_ip": "192.168.4.1",
  "ap_mask": "255.255.255.0",
  "ap_gw": "192.168.4.1",
  "ap_ssid": "Manech_AP",
  "ap_password": "password123",
  "wifi_ssid": "Home_Wifi",
  "wifi_password": "wifi_password"
}
```

### 3. Animate Pixels
Controls the color of specific LEDs on the node.

- **URL:** `http://<node_ip>/animate`
- **Method:** `POST`
- **Body:** A JSON array of animation objects.
```json
[
  {
    "color": {
      "r": 255,
      "g": 0,
      "b": 0
    },
    "indexes": [0, 1, 2, 3, 4, 5]
  }
]
```
- **Note on Indexing:** The `indexes` provided in the request must be the local hardware indices of the LEDs on that specific node (typically ranging from `min_leds` to `max_leds`).
