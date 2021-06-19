package com.manech.app.view.mnode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiListItemAdapter extends RecyclerView.Adapter<WifiListItemViewHolder>{
    private final JSONArray availableWifiArray;
    private WifiSettingsFragment listener;
    public WifiListItemAdapter(JSONArray availableWifiArray, WifiSettingsFragment listener) {

        this.availableWifiArray = availableWifiArray;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WifiListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        view.setOnClickListener(listener);
        return new WifiListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiListItemViewHolder holder, int position) {
        JSONObject availableWifi;
        try {
            availableWifi = availableWifiArray.getJSONObject(position);
            holder.getTvWifiName().setText(availableWifi.getString("ssid"));
            holder.getTvWifiSignal().setText(availableWifi.getString("signal")+"%");
            holder.getTvWifiSecure().setText(availableWifi.getString("open").equals("Y")?"Open":"Encrypted");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getItemViewType(final int position) {
        return R.layout.wifi_item_view;
    }
    @Override
    public int getItemCount() {
        return availableWifiArray.length();
    }


    public JSONObject getItem(int position) throws JSONException {
        return availableWifiArray.getJSONObject(position);
    }
}
