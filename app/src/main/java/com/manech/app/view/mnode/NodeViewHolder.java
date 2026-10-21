package com.manech.app.view.mnode;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.app.R;


public class NodeViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
    NodeClickListener listener;
    private TextView tvHostname;
    private TextView tvState;
    private TextView tvIP;
    private TextView tvDescription;
    private ImageView imgHighlight;
    private ImageView imgDelete;
    private ImageView imgDuplicate;
    private ImageView imgQrCode;

    public NodeViewHolder(@NonNull View itemView,NodeClickListener listener) {
        super(itemView);
        tvHostname = itemView.findViewById(R.id.tvHostname);
        tvState = itemView.findViewById(R.id.tvState);
        tvIP = itemView.findViewById(R.id.tvIP);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        imgHighlight =itemView.findViewById(R.id.imgHighlight);
        imgHighlight.setOnClickListener(this);
        imgDelete =itemView.findViewById(R.id.imgDelete);
        imgDelete.setOnClickListener(this);
        imgDuplicate =itemView.findViewById(R.id.imgDuplicate);
        imgDuplicate.setOnClickListener(this);
        imgQrCode = itemView.findViewById(R.id.imgQrCode);
        imgQrCode.setOnClickListener(this);
        this.listener=listener;
    }

    public TextView getTvHostname(){
        return tvHostname;
    }

    public TextView getTvState() {
        return tvState;
    }

    public TextView getTvIP() {
        return tvIP;
    }

    public TextView getTvDescription() {
        return tvDescription;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.imgHighlight){
            listener.highlightClicked(this.getLayoutPosition());
        } else if(id == R.id.imgDelete){
            listener.deleteClicked(this.getLayoutPosition());
        } else if(id == R.id.imgDuplicate){
            listener.duplicateClicked(this.getLayoutPosition());
        } else if(id == R.id.imgQrCode){
            listener.qrClicked(this.getLayoutPosition());
        }
    }
}

