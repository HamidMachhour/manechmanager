package com.manech.app.view.mnode;

import android.view.View;
import android.widget.TextView;
import com.manech.app.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WifiListItemViewHolder extends RecyclerView.ViewHolder{
    private TextView tvWifiName;
    private TextView tvWifiSignal;
    private TextView tvWifiSecure;
    public WifiListItemViewHolder(@NonNull View itemView) {
        super(itemView);
     tvWifiName=itemView.findViewById(R.id.tvWifiName);
     tvWifiSignal=itemView.findViewById(R.id.tvSignal);
     tvWifiSecure=itemView.findViewById(R.id.tvSecure);
    }

    public TextView getTvWifiSecure() {
        return tvWifiSecure;
    }

    public TextView getTvWifiSignal() {
        return tvWifiSignal;
    }

    public TextView getTvWifiName() {
        return tvWifiName;
    }
}
