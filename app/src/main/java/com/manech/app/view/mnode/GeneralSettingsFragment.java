package com.manech.app.view.mnode;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.manech.app.R;
import com.manech.app.data.ManechNode;


public class GeneralSettingsFragment extends Fragment implements Observer<ManechNode>
, MNodeForm {
    private ManechNode node;
    // general settings
    private TextView tvState;
    private EditText txtName;
    private EditText txtDescription;
    private EditText txtHttpLogin;
    private EditText txtHttpPwd;
    private EditText txtMinIndex;
    private EditText txtMaxIndex;
    private EditText txtMinPixel;
    private EditText txtMaxPixel;

    public GeneralSettingsFragment() {
        // Required empty public constructor
    }

private static  GeneralSettingsFragment instance;
    public static GeneralSettingsFragment newInstance() {
        if(instance==null)
            instance=new GeneralSettingsFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_general_settings, container, false);
        initForm(view);
        return view;
    }

    private void initForm(View view) {
        tvState=view.findViewById(R.id.tvState);
        txtName =view.findViewById(R.id.txtName);
        txtDescription=view.findViewById(R.id.txtDescription);
        txtHttpLogin=view.findViewById(R.id.txtHttpLogin);
        txtHttpPwd=view.findViewById(R.id.txtHttpPwd);
        txtMinIndex=view.findViewById(R.id.txtMinIndex);
        txtMaxIndex=view.findViewById(R.id.txtMaxIndex);
        txtMinPixel=view.findViewById(R.id.txtMinPixel);
        txtMaxPixel=view.findViewById(R.id.txtMaxPixel);
    }

    @Override
    public void onChanged(ManechNode node) {
        this.node=node;
        if(this.isVisible()){
            nodeToForm();
        }
    }


    @Override
    public void nodeToForm() {
        if (node != null) {
            if (node.getState() == ManechNode.ONLINE_STATE) {
                tvState.setTextColor(Color.GREEN);
                tvState.setText("ONLINE");
            } else {
                tvState.setTextColor(Color.RED);
                tvState.setText("OFFLINE");
            }
            txtName.setText(node.getName());
            txtHttpLogin.setText(node.getHttpLogin());
            txtHttpPwd.setText(node.getHttpPassword());
            txtMinIndex.setText("" + node.getMinIndex());
            txtMaxIndex.setText("" + node.getMaxIndex());
            txtMinPixel.setText("" + node.getMinPixel());
            txtMaxPixel.setText("" + node.getMaxPixel());
            txtDescription.setText(node.getDescription());
        }
    }

    @Override
    public void formToNode() {
        if (node != null) {
            node.setName(txtName.getText().toString());
            node.setHttpLogin(txtHttpLogin.getText().toString());
            node.setHttpPassword(txtHttpPwd.getText().toString());
            node.setPixels(Integer.parseInt(txtMinPixel.getText().toString()),Integer.parseInt(txtMaxPixel.getText().toString()));
            node.setIndexes(Integer.parseInt(txtMinIndex.getText().toString()),Integer.parseInt(txtMaxIndex.getText().toString()));
            node.setDescription(txtDescription.getText().toString());
        }
    }

}