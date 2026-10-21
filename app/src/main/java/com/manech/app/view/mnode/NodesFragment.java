package com.manech.app.view.mnode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.manech.app.QrScannerActivity;
import com.manech.app.R;
import com.manech.app.data.ManechNode;
import com.manech.app.helper.MyColor;
import com.manech.app.helper.QrCodeHelper;
import com.manech.app.model.Injection;
import com.manech.app.model.MNodeViewModel;
import com.manech.app.service.ManechService;
import com.manech.app.view.user.UserFragment;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

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

        adapter = new ManechNodeAdapter(new ArrayList<>(), NodesFragment.this);
        nodesListView.setAdapter(adapter);

        mNodeViewModel.getNodes().observe(this, new Observer<List<ManechNode>>() {

            @Override
            public void onChanged(List<ManechNode> manechNodes) {

                if (manechNodes != null) {
                    adapter.updateData(manechNodes);
                }
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
        if (mDialog != null && mDialog.isShowing() && isAdded()) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (id == R.id.action_scan_qr) {
            scanQrCode();
            return true;
        }
        if (id == R.id.action_print_all_qr) {
            printAllQrCodes();
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
        // Removed showProgressDialog() here to prevent UI flickering
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
                        // Removed showProgressDialog() here to prevent UI flickering
                        mNodeViewModel.fetchNodes(userId, false);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void qrClicked(int p) {
        ManechNode node = adapter.getItem(p);
        String configJson = node.prepareConfiguration().toString();
        Bitmap qrBitmap = QrCodeHelper.generateQrCode(configJson, 500);
        
        new AlertDialog.Builder(requireActivity())
                .setTitle("Node Configuration QR")
                .setMessage("Scan this code to import node settings")
                .setView(createQrImageView(qrBitmap))
                .setPositiveButton("Save as PDF", (dialog, which) -> {
                    File pdf = QrCodeHelper.generateSingleNodePdf(node);
                    if (pdf != null) {
                        Toast.makeText(getContext(), "PDF saved to Downloads: " + pdf.getName(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private View createQrImageView(Bitmap bitmap) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(500, 500));
        return imageView;
    }

    private void scanQrCode() {
        Intent intent = new Intent(getContext(), QrScannerActivity.class);
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && resultCode == MainActivity.RESULT_OK) {
            String qrData = data.getStringExtra("SCAN_RESULT");
            if (qrData != null) {
                ManechNode node = QrCodeHelper.parseConfigJson(qrData);
                if (node != null) {
                    node.setUserId(userId); // Assign current user
                    mNodeViewModel.insert(node);
                    Toast.makeText(getContext(), "Node imported successfully!", Toast.LENGTH_SHORT).show();
                    mNodeViewModel.fetchNodes(userId, false);
                } else {
                    Toast.makeText(getContext(), "Invalid QR Code format", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == 1001 && resultCode == MainActivity.RESULT_CANCELED) {
            Toast.makeText(getContext(), "Scan canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void printAllQrCodes() {
        showProgressDialog();
        java.util.List<ManechNode> nodes = adapter.getNodesList();
        File pdfFile = QrCodeHelper.generateConfigPdf(nodes);
        if (pdfFile != null && pdfFile.exists()) {
            Toast.makeText(getContext(), "PDF saved to Downloads/ManechNodes_QR.pdf", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Failed to generate PDF. Check storage permissions.", Toast.LENGTH_SHORT).show();
        }
        hideProgressDialog();
    }
}

