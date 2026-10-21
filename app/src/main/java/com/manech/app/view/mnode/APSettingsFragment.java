package com.manech.app.view.mnode;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.manech.app.R;
import com.manech.app.data.ManechNode;

import org.json.JSONObject;

public class APSettingsFragment extends Fragment  implements Observer<ManechNode> , MNodeForm {
    private ManechNode node;

    // Access Point settings
    private EditText txtAPIP;
    private EditText txtAPMask;
    private EditText txtAPGateway;
    private EditText txtAPName;
    private EditText txtAPKey;
    private TextView tvStations;

    private final InputFilter ipFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart)
                        + source.subSequence(start, end)
                        + destTxt.substring(dend);
                if (!resultingTxt
                        .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (String split : splits) {
                        if (Integer.valueOf(split) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        }
    };


    public APSettingsFragment() {
        // Required empty public constructor
    }
    private static APSettingsFragment instance;
    public static APSettingsFragment newInstance() {
        if(instance==null)
            instance = new APSettingsFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_a_p_settings, container, false);
        initForm(view);
        return view;
    }

    @Override
    public void onChanged(ManechNode node) {
        this.node=node;
        if(this.isVisible()){
            nodeToForm();
        }
    }


    private void initForm(View view) {
        txtAPIP =view.findViewById(R.id.txtAPIP);
        txtAPIP.setFilters(new InputFilter[]{ipFilter});
        txtAPMask =view.findViewById(R.id.txtAPMask);
        txtAPMask.setFilters(new InputFilter[]{ipFilter});
        txtAPGateway =view.findViewById(R.id.txtAPGateway);
        txtAPGateway.setFilters(new InputFilter[]{ipFilter});
        txtAPName =view.findViewById(R.id.txtAPName);
        txtAPKey=view.findViewById(R.id.txtAPKey);
        tvStations=view.findViewById(R.id.tvStations);

    }

    @Override
    public void nodeToForm() {
        if(node!=null) {
            txtAPIP.setText(node.getApIP());
            txtAPMask.setText(node.getApMask());
            txtAPGateway.setText(node.getApGateway());
            txtAPName.setText(node.getApName());
            txtAPKey.setText(node.getApKey());
            tvStations.setText(""+node.getNbStations());
        }
    }

    @Override
    public void formToNode() {
        if (node != null) {
            node.setApIP(txtAPIP.getText().toString());
            node.setApMask(txtAPMask.getText().toString());
            node.setApGateway(txtAPGateway.getText().toString());
            node.setApName(txtAPName.getText().toString());
            node.setApKey(txtAPKey.getText().toString());
        }
    }
}