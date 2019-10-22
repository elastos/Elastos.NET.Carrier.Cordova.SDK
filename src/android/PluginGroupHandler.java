 /*
  * Copyright (c) 2018 Elastos Foundation
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  */

package org.elastos.trinity.plugins.carrier;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.elastos.carrier.AbstractGroupHandler;
import org.elastos.carrier.Group;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class PluginGroupHandler extends AbstractGroupHandler{
    private static String TAG = "PluginGroupHandler";
    private String groupId ;
    Group mGroup ;
    private CallbackContext mCallbackContext;

    PluginGroupHandler(CallbackContext callbackContext) {
        this.mCallbackContext = callbackContext ;
    }

    void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    void setGroup(Group mGroup) {
        this.mGroup = mGroup;
    }

    @Override
    public void onGroupConnected(Group group) {
        JSONObject r = new JSONObject();
        try {
            r.put("name", "onConnection");
            sendEvent(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGroupMessage(Group group, String from, byte[] message) {
        JSONObject r = new JSONObject();
        String messageData = new String(message, StandardCharsets.UTF_8);
        try {
            r.put("name", "onGroupMessage");
            r.put("from", from);
            r.put("message",messageData);
            sendEvent(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGroupTitle(Group group, String from, String title) {
        JSONObject r = new JSONObject();
        try {
            r.put("name", "onGroupTitle");
            r.put("from", from);
            r.put("title", title);
            sendEvent(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeerName(Group group, String peerId, String peerName) {
        JSONObject r = new JSONObject();
        try {
            r.put("name", "onPeerName");
            r.put("peerId", peerId);
            r.put("peerName", peerName);
            sendEvent(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeerListChanged(Group group) {
        JSONObject r = new JSONObject();
        try {
            r.put("name", "onPeerListChanged");
            sendEvent(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendEvent(JSONObject info) throws JSONException {
        info.put("groupId", groupId);
        if (mCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, info);
            result.setKeepCallback(true);
            mCallbackContext.sendPluginResult(result);
        }
    }
}

