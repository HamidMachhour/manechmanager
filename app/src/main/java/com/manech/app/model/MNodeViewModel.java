package com.manech.app.model;

import android.util.Base64;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.manech.app.data.ManechNode;
import com.manech.app.helper.MyColor;
import com.manech.app.repository.ManechNodeRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class MNodeViewModel extends ViewModel {

    // REPOSITORIES
    private ManechNodeRepository manechNodeRepository;
    private Executor executor;

    // DATA
    private MutableLiveData<List<ManechNode>> nodes = new MutableLiveData<List<ManechNode>>();
    private MutableLiveData<ManechNode> currentNode = new MutableLiveData<ManechNode>();

    public void setManechNodeRepository(ManechNodeRepository manechNodeRepository) {
        this.manechNodeRepository = manechNodeRepository;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public MutableLiveData<List<ManechNode>> getNodes() {
        return nodes;
    }

    public void fetchNodes(long userId, boolean trackState) {
        executor.execute(() ->
                this.nodes.postValue(manechNodeRepository.getAllNodes(userId, trackState))
        );
    }


    public void insert(ManechNode node) {
        executor.execute(() -> {
            manechNodeRepository.insert(node);
        });
    }

    public void deleteNode(long nodeId) {
        executor.execute(() -> {
            manechNodeRepository.delete(nodeId);
        });
        fetchNode(nodeId, true);
    }

    public void fetchNode(long nodeId, boolean trackState) {
        executor.execute(() ->
                this.currentNode.postValue(manechNodeRepository.fetchNode(nodeId, trackState))
        );
    }

    public void update(ManechNode node) {
        executor.execute(() -> {
            manechNodeRepository.update(node);
        });
        fetchNode(node.getId(), true);
    }

    public void deleteAllNodes(long userId) {
        executor.execute(() -> {
            manechNodeRepository.deleteAll(userId);
        });
        fetchNodes(userId, false);
    }

    public MutableLiveData<ManechNode> getCurrentNode() {
        return currentNode;
    }

    public void populate(long userId) {
        executor.execute(() -> {
            manechNodeRepository.populate(userId);
            fetchNodes(userId, false);

        });
    }

    public void duplicateNode(long nodeId) {
        executor.execute(() -> {
            this.currentNode.postValue(manechNodeRepository.duplicate(nodeId));
        });
    }

    private Response.ErrorListener createBackupErrorListener(ManechNode node) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                node.setDescription("====Backup Error=====\n" + error.toString()
                        + "\n=====================\n" + node.getDescription());
                node.setState(ManechNode.OFFLINE_STATE);
                executor.execute(() -> {
                    currentNode.postValue(node);
                });
            }
        };
    }

    private Response.Listener<JSONObject> createBackupSuccessListener(ManechNode node) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                node.backupConfiguration(response);
                executor.execute(() -> {
                    currentNode.postValue(node);
                });
            }
        };
    }

    public void backupConfiguration(long nodeId) {
        executor.execute(() -> {
                    ManechNode node = manechNodeRepository.fetchNode(nodeId, true);
                    if (node != null && node.getState() == ManechNode.ONLINE_STATE) {
                        String url = "http://" + node.getDefaultIP() + "/get";
                        JSONObject headers = new JSONObject();
                        final CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, headers,
                                this.createBackupSuccessListener(node),
                                this.createBackupErrorListener(node),
                                node.getHttpLogin() + ":" + node.getHttpPassword());
                        manechNodeRepository.getRequestQueue().add(customRequest);
                    } else {
                        currentNode.postValue(node);
                    }
                }
        );
    }


    private Response.Listener<JSONObject> createConfigureSuccessListener(ManechNode node) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                update(node);
            }
        };
    }

    private Response.ErrorListener createConfigureErrorListener(ManechNode node) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*node.setDescription("====Configuration Error=====\n" + error.toString()
                        + "\n=====================\n" + node.getDescription());*/
                node.setState(ManechNode.OFFLINE_STATE);
                executor.execute(() -> {
                    currentNode.postValue(node);
                });
            }
        };
    }

    public void applyConfiguration(ManechNode node) {
        executor.execute(() -> {
                    ManechNode oldNode = manechNodeRepository.fetchNode(node.getId(), true);
                    JSONObject toSend = node.prepareConfiguration();
                    if (toSend != null && oldNode != null && oldNode.getState() == ManechNode.ONLINE_STATE) {
                        String url = "http://" + oldNode.getDefaultIP() + "/set";

                        final CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, toSend,
                                createConfigureSuccessListener(node), createConfigureErrorListener(oldNode),
                                oldNode.getHttpLogin() + ":" + oldNode.getHttpPassword());
                        manechNodeRepository.getRequestQueue().add(customRequest);
                    } else {
                        currentNode.postValue(oldNode);
                    }
                }
        );
    }


    public void highlight(ManechNode node, MyColor c) {
        if (node == null) return;

        int []indexes=new int[node.getMaxIndex()-node.getMinIndex()+1];
        for(int i=0;i<indexes.length;indexes[i]=i+node.getMinIndex(),i++);

        if (node.isHighlighted()) {
            animate(node,indexes, new MyColor(0, 0, 0));
        } else {
            animate(node, indexes, c);
        }
        node.setHighlighted(!node.isHighlighted());
    }

    public JsonArrayRequest prepareRequest(ManechNode node, int[] indexes, MyColor c) {
        JSONArray jsonArray;
        if (node.getDefaultIP() != null && (jsonArray = node.preparePixels(indexes, c)) != null) {
            String url = "http://" + node.getDefaultIP() + "/animate";
            final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
                    null,
                    null) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String credentials = node.getHttpLogin() + ":" + node.getHttpPassword();
                    Map<String, String> headers = new HashMap<>();
                    String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", auth);
                    return headers;
                }
            };
            return jsonArrayRequest;
        }
        return null;
    }

    public void animate(ManechNode node,int[] indexes, MyColor c) {
        executor.execute(() -> {
            if (node != null) {
                JsonArrayRequest jsonArrayRequest = prepareRequest(node, indexes, c);
                if (jsonArrayRequest != null) {
                    manechNodeRepository.getRequestQueue().add(jsonArrayRequest);
                }
            }
        });

    }

    public void locate(long userId, int[] indexes, MyColor c) {
        executor.execute(() -> {
            final List<ManechNode> allNodes = manechNodeRepository.getNodesByIndexes(userId,indexes, false);
            final Iterator<ManechNode> iterator = allNodes.iterator();
            while(iterator.hasNext()) {
                JsonArrayRequest jsonArrayRequest = prepareRequest(iterator.next(), indexes, c);
                if (jsonArrayRequest != null) {
                    manechNodeRepository.getRequestQueue().add(jsonArrayRequest);
                }
            }
        });
     }


    private class CustomRequest extends JsonObjectRequest {
        private static final int MY_SOCKET_TIMEOUT_MS = 3000;
        private String credentials;

        public CustomRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener, String credentials) {
            super(method, url, jsonRequest, listener, errorListener);
            this.credentials = credentials;
           /* this.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", auth);
            return headers;
        }
    }
}