package com.manech.app.repository;

import com.android.volley.RequestQueue;
import com.manech.app.dao.ManechNodeDAO;
import com.manech.app.data.ManechNode;

import java.io.IOException;
import java.util.List;

public class ManechNodeRepository {
    private final ManechNodeDAO manechNodeDAO;
    private RequestQueue requestQueue;

    public ManechNodeRepository(ManechNodeDAO manechNodeDAO, RequestQueue requestQueue) {
        this.manechNodeDAO = manechNodeDAO;
        this.requestQueue = requestQueue;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public List<ManechNode> getAllNodes(long userId, boolean trackState) {
        final List<ManechNode> manechNodes = this.manechNodeDAO.nodeByUser(userId);
        if (trackState)
            trackStateFor(manechNodes);
        return manechNodes;
    }

    private void trackStateFor(List<ManechNode> manechNodes) {
        for (ManechNode node : manechNodes) {
            trackState(node);
        }
    }

    public boolean isReachable(String ip) {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + ip);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void trackState(ManechNode node) {
        if (isReachable(node.getWifiIP())) {
            node.setDefaultIP(node.getWifiIP());
            node.setState(ManechNode.ONLINE_STATE);
        } else
        if (isReachable(node.getApIP())){
            node.setDefaultIP(node.getApIP());
            node.setState(ManechNode.ONLINE_STATE);
        }
        else
        node.setState(ManechNode.OFFLINE_STATE);

    }

    public void insert(ManechNode node) {
        manechNodeDAO.insert(node);
    }

    public void delete(long nodeId) {
        manechNodeDAO.delete(nodeId);
    }

    public void update(ManechNode node) {
        manechNodeDAO.update(node);
    }

    public void deleteAll(long userId) {
        this.manechNodeDAO.deleteAll(userId);
    }

    public ManechNode fetchNode(long nodeId, boolean trackState) {
        ManechNode node = manechNodeDAO.getNode(nodeId);
        if (node!=null && trackState)
            trackState(node);
        return node;
    }

    public ManechNode duplicate(long nodeId) {
        ManechNode current = fetchNode(nodeId, false);
        insert(current.clone());
        return current;
    }

    public void populate(long userId) {
        insert(new ManechNode(userId,"manech","","admin","12345678",
                0,0,"Manech","12345678","192.168.4.1","255.255.255.0","192.168.4.1",
                "","","","",""));
    }

    public List<ManechNode> getNodesByIndexes(long userId, int[] indexes, boolean trackState) {
        int min, max;
        if(indexes==null|| indexes.length==0) return null;
        min=indexes[0]; max=indexes[0];
        for(int i=1; i<indexes.length;i++ ){
            if(indexes[i]>max) max=indexes[i];
            if(indexes[i]<min) min=indexes[i];
        }
        final List<ManechNode> manechNodes = this.manechNodeDAO.nodeByMinMaxIndexes(userId,min,max);
        if (trackState)
            trackStateFor(manechNodes);
        return manechNodes;
    }
}
