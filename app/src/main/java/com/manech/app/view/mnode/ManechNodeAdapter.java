package com.manech.app.view.mnode;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.app.R;
import com.manech.app.data.ManechNode;

import java.util.ArrayList;
import java.util.List;

public class ManechNodeAdapter extends RecyclerView.Adapter<NodeViewHolder> implements Filterable {

    private List<ManechNode> nodesList;
    private List<ManechNode> allNodes;
    private final NodesFragment listListener;

    public ManechNodeAdapter(List<ManechNode> nodesList, NodesFragment listener) {
        this.nodesList = nodesList;
        this.allNodes = new ArrayList<>(nodesList);

        this.listListener = listener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.node_view;
    }

    @NonNull
    @Override
    public NodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        view.setOnClickListener(this.listListener);
        return new NodeViewHolder(view, new NodeClickListener() {
            @Override
            public void highlightClicked(int p) {
                listListener.highlightClicked(p);
            }

            @Override
            public void duplicateClicked(int p) {
                listListener.duplicateClicked(p);
            }

            @Override
            public void deleteClicked(int p) {
                listListener.deleteClicked(p);
            }

            @Override
            public void qrClicked(int p) {
                listListener.qrClicked(p);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull NodeViewHolder holder, int position) {
        ManechNode node = nodesList.get(position);
        holder.getTvHostname().setText(node.getName());
        String ip = "IP/MASK:";
        if (node.isAccessPoint()) {
            ip += node.getApIP() + "/" + node.getApMask() ;
        } else {
            ip += node.getWifiIP() + "/" + node.getWifiMask() ;
        }
        holder.getTvIP().setText(ip);
        String description = node.getDescription();
        String desc = "Indexes: [" + node.getMinIndex() + ", " + node.getMaxIndex() + "]\n";
        desc += "Pixels: [" + node.getMinPixel() + ", " + node.getMaxPixel() + "]\n";

        if (description != null)
            desc += description.substring(0, description.length() % 30) + " ...";
        holder.getTvDescription().setText(desc);
        if (node.getState() == ManechNode.ONLINE_STATE) {
            holder.getTvState().setTextColor(Color.GREEN);
            holder.getTvState().setText("ONLINE");
        } else {
            holder.getTvState().setTextColor(Color.RED);
            holder.getTvState().setText("OFFLINE");
        }

    }

    @Override
    public int getItemCount() {
        return nodesList.size();
    }

    @Override
    public Filter getFilter() {
        return nodesFilter;
    }


    private Filter nodesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ManechNode> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allNodes);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ManechNode node : allNodes) {
                    String text = node.getName() + " " + node.getDescription() + " " + node.getApIP() +
                            " " + node.getApName() + " " + (((node.getState() == node.ONLINE_STATE) ? "online" : "offline"));
                    if (text.toLowerCase().trim().contains(filterPattern)) {
                        filteredList.add(node);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            nodesList.clear();
            nodesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public ManechNode getItem(int itemPosition) {
        return nodesList.get(itemPosition);
    }

    public List<ManechNode> getNodesList() {
        return nodesList;
    }

    public void updateData(List<ManechNode> newList) {
        this.nodesList = newList;
        this.allNodes = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

}


