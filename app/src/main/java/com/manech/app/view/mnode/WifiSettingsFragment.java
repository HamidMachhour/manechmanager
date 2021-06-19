package com.manech.app.view.mnode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.app.R;
import com.manech.app.data.ManechNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiSettingsFragment extends Fragment implements Observer<ManechNode>, MNodeForm, View.OnClickListener  {
    private ManechNode node;
    // WIFI settings
    private TextView tvWifiIP;
    private TextView tvWifiMask;
    private TextView tvWifiGateway;
    private EditText txtWifiName;
    private EditText txtWifiKey;

    private RecyclerView wifiListView;
    private WifiListItemAdapter adapter;

    public WifiSettingsFragment() {
        // Required empty public constructor
    }

    private static WifiSettingsFragment instance;

    public static WifiSettingsFragment newInstance() {
        if (instance == null)
            instance = new WifiSettingsFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_settings, container, false);
        initForm(view);
        return view;
    }


    @Override
    public void onChanged(ManechNode node) {
        this.node = node;
        if (this.isVisible()) {
            nodeToForm();
        }
    }

    private void initForm(View view) {
        tvWifiIP = view.findViewById(R.id.tvWifiIP);
        tvWifiMask = view.findViewById(R.id.tvWifiMask);
        tvWifiGateway = view.findViewById(R.id.tvWifiGateway);
        txtWifiName = view.findViewById(R.id.txtWifiName);
        txtWifiKey = view.findViewById(R.id.txtWifiKey);
        wifiListView=view.findViewById(R.id.wifi_list_view);
        wifiListView.setHasFixedSize(true);
        wifiListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void nodeToForm() {
        if (node != null) {
            tvWifiIP.setText(node.getWifiIP());
            tvWifiMask.setText(node.getWifiMask());
            tvWifiGateway.setText(node.getWifiGateway());
            txtWifiName.setText(node.getWifiName());
            txtWifiKey.setText(node.getWifiKey());
            final JSONArray availableWifi = node.getAvailableWifi();
            if (availableWifi != null) {
                adapter = new WifiListItemAdapter(availableWifi, this);
                wifiListView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void formToNode() {
        if (node != null) {
            node.setWifiName(txtWifiName.getText().toString());
            node.setWifiKey(txtWifiKey.getText().toString());
            node.setWifiIP(tvWifiIP.getText().toString());
            node.setWifiMask(tvWifiMask.getText().toString());
            node.setWifiGateway(tvWifiGateway.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            final JSONObject item = adapter.getItem(wifiListView.getChildLayoutPosition(v));
            txtWifiName.setText(item.getString("ssid"));
            txtWifiKey.setText("");
            txtWifiKey.requestFocus();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}