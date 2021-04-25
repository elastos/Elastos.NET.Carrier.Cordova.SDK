 /*
  * Copyright (c) 2021 Elastos Foundation
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

package org.elastos.essentials.plugins.carrier;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import org.elastos.carrier.*;

public class PluginReceiptHandler implements FriendMessageReceiptHandler {
	private int mHandlerId;
	public CallbackContext mCallbackContext = null;

	public PluginReceiptHandler(int id, CallbackContext callbackContext) {
		this.mHandlerId = id;
		this.mCallbackContext = callbackContext;
	}

	private void sendEvent(JSONObject info) throws JSONException {
		info.put("id", mHandlerId);
		if (mCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			result.setKeepCallback(true);
			mCallbackContext.sendPluginResult(result);
		}
	}

    @Override
    public void onReceipt(long messageid, ReceiptState state)  {
        JSONObject r = new JSONObject();
        try {
            r.put("messageId", messageid);
            r.put("state", state.value());
            sendEvent(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

