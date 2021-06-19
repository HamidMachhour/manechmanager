package com.manech.app.view.mnode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.manech.app.R;
import com.manech.app.data.ManechNode;
import com.manech.app.helper.MyColor;
import com.manech.app.model.Injection;
import com.manech.app.model.MNodeViewModel;

import java.util.ArrayList;
import java.util.List;


public class NodeFragment extends Fragment implements Observer<ManechNode> {
    private ProgressDialog mDialog;
    private long nodeId;
    //private long userId;
    private ManechNode node=null;
    private MNodeViewModel mNodeViewModel;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    Adapter adapter;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    ((MNodeForm)adapter.getItem(tab.getPosition())).nodeToForm();
                }catch (Exception e){
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((MNodeForm)adapter.getItem(tab.getPosition())).formToNode();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                ((MNodeForm)adapter.getItem(tab.getPosition())).formToNode();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_node, container, false);
        // Setting ViewPager for each Tabs
        viewPager = (ViewPager) view.findViewById(R.id.nodeFragmentViewPager);
        // Set Tabs inside Toolbar
        tabLayout = (TabLayout) view.findViewById(R.id.nodeFragmentTabLayout);

        //set viewPager
        adapter = new Adapter(getChildFragmentManager());

        final GeneralSettingsFragment generalSettingsFragment =GeneralSettingsFragment.newInstance();
        adapter.addFragment(generalSettingsFragment, getString(R.string.general_settings));
        final APSettingsFragment apSettingsFragment=APSettingsFragment.newInstance();
        adapter.addFragment(apSettingsFragment, getString(R.string.ap_settings));
        final WifiSettingsFragment wifiSettingsFragment=WifiSettingsFragment.newInstance();
        adapter.addFragment(wifiSettingsFragment, getString(R.string.wifi_settings));
        viewPager.setAdapter(adapter);

        mNodeViewModel = Injection.provideViewModelFactory(requireActivity()).create(MNodeViewModel.class);
        mNodeViewModel.getCurrentNode().observe(this,generalSettingsFragment);
        mNodeViewModel.getCurrentNode().observe(this,apSettingsFragment);
        mNodeViewModel.getCurrentNode().observe(this,wifiSettingsFragment);
        mNodeViewModel.getCurrentNode().observe(this,this);

        nodeId = getArguments().getLong("nodeId", -1);
        showProgressDialog();
        mNodeViewModel.fetchNode(nodeId, true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.node_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
        if (id == R.id.action_save) {
            saveAction();
            return true;
        }
        if (id == R.id.action_download) {
            backupConfigurationAction();
            return true;
        }
        if (id == R.id.action_upload) {
            applyConfigurationAction();
            return true;
        }
        if (id == R.id.action_highlight) {
            highlightAction();
            return true;
        }
        if (id == R.id.action_duplicate) {
            duplicateAction();
            return true;
        }
        if (id == R.id.action_delete_node) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.delete_single_node))
                    .setMessage(R.string.delete_node_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            showProgressDialog();
                            mNodeViewModel.deleteNode(nodeId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog() {
        mDialog = ProgressDialog.show(getContext(),"Please wait...", "Retrieving data ...", true);
    }

    private void hideProgressDialog() {
        mDialog.dismiss();
    }

    private void saveAction() {
        showProgressDialog();
        try {
            for (Fragment f : adapter.mFragmentList) {
                ((MNodeForm) f).formToNode();
            }
        }catch (Exception e){
        }
        mNodeViewModel.update(node);
        mNodeViewModel.fetchNode(nodeId,true);
    }

    private void backupConfigurationAction() {
        showProgressDialog();
        mNodeViewModel.backupConfiguration(nodeId) ;
    }

    private void applyConfigurationAction() {
        showProgressDialog();
        try {
            for (Fragment f : adapter.mFragmentList) {
                ((MNodeForm) f).formToNode();
            }
        }catch (Exception e){
        }
        mNodeViewModel.applyConfiguration(node) ;
    }

    private void duplicateAction() {
        showProgressDialog();
        mNodeViewModel.duplicateNode(nodeId);
    }

    private void highlightAction() {
        mNodeViewModel.highlight(node,MyColor.random(0,200)) ;
    }

    @Override
    public void onChanged(ManechNode node) {
        hideProgressDialog();
        if (node != null) {
            this.node=node;
        } else {
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
