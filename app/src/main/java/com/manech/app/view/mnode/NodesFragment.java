package com.manech.app.view.mnode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.app.LoginActivity;
import com.manech.app.MainActivity;
import com.manech.app.R;
import com.manech.app.data.ManechNode;
import com.manech.app.helper.MyColor;
import com.manech.app.model.Injection;
import com.manech.app.model.MNodeViewModel;
import com.manech.app.service.ManechService;
import com.manech.app.view.user.UserFragment;

import java.util.List;

public class NodesFragment extends Fragment implements View.OnClickListener {
    private MNodeViewModel mNodeViewModel;
    private RecyclerView nodesListView;
    private ManechNodeAdapter adapter;
    private long userId;
    private ProgressDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nodes, container, false);

        nodesListView = view.findViewById(R.id.nodes_list_view);
        nodesListView.setHasFixedSize(true);
        nodesListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mNodeViewModel = Injection.provideViewModelFactory(requireActivity()).create(MNodeViewModel.class);
        mNodeViewModel.getNodes().observe(this, new Observer<List<ManechNode>>() {

            @Override
            public void onChanged(List<ManechNode> manechNodes) {

                //update recycleview
                adapter = new ManechNodeAdapter(manechNodes, NodesFragment.this);
                nodesListView.setAdapter(adapter);
                hideProgressDialog();
            }
        });

        showProgressDialog();
        userId = getActivity().getApplicationContext()
                .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getLong(getString(R.string.saved_user_id), -1L);

        mNodeViewModel.fetchNodes(userId, true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    public void onClick(View v) {
        ManechNode node = adapter.getItem(nodesListView.getChildLayoutPosition(v));
        initNodeFragment(node.getId());
    }

    public void initNodeFragment(long nodeId) {
        getActivity()
                .getApplicationContext()
                .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .edit()
                .putLong(getString(R.string.nodeid), nodeId).apply();

        String backStateName = NodeFragment.class.getName();

        FragmentManager manager = requireActivity().getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main_frame_layout, new NodeFragment(), backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }

    }

    private void showProgressDialog() {
        mDialog = ProgressDialog.show(getContext(), getString(R.string.please_wait), getString(R.string.loading_data), true);
    }

    private void hideProgressDialog() {
        mDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_track_state) {
            showProgressDialog();
            mNodeViewModel.fetchNodes(userId, true);
        }
        if (id == R.id.action_add) {
            showProgressDialog();
            mNodeViewModel.populate(userId);
            return true;
        }
        if (id == R.id.action_clear) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.delete_all))
                    .setMessage(R.string.delete_nodes_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            showProgressDialog();
                            mNodeViewModel.deleteAllNodes(userId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        if (id == R.id.action_manage_account) {
            initUserFragment();
            return true;
        }
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        ((MainActivity)getActivity()).logout();
    }

    private void initUserFragment() {
        String backStateName = UserFragment.class.getName();
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main_frame_layout, new UserFragment(), backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.nodes_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void highlightClicked(int p) {
        ManechNode node = adapter.getItem(p);
        mNodeViewModel.highlight(node, MyColor.random(0, 200));
    }

    public void duplicateClicked(int p) {
        ManechNode node = adapter.getItem(p);
        mNodeViewModel.duplicateNode(node.getId());
        showProgressDialog();
        mNodeViewModel.fetchNodes(userId, false);
    }

    public void deleteClicked(int p) {
        ManechNode node = adapter.getItem(p);
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_single_node))
                .setMessage(R.string.delete_node_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mNodeViewModel.deleteNode(node.getId());
                        showProgressDialog();
                        mNodeViewModel.fetchNodes(userId, false);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}