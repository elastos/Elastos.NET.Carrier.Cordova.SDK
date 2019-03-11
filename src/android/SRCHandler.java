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
import org.elastos.carrier.session.Session;
import org.elastos.carrier.session.SessionRequestCompleteHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class SRCHandler implements SessionRequestCompleteHandler {
	private static String TAG = "SRCHandler";

	private int mHandlerId;
	public CallbackContext mCallbackContext = null;

	public SRCHandler(int id, CallbackContext callbackContext) {
		this.mHandlerId = id;
		this.mCallbackContext = callbackContext;
	}

	private void sendEvent(JSONObject info) throws JSONException {
		info.put("id", mHandlerId);
		if (mCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			// result.setKeepCallback(true);
			mCallbackContext.sendPluginResult(result);
		}
	}

	@Override
	public void onCompletion(Session session, int status, String reason, String sdp) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onCompletion");
			r.put("status", status);
			r.put("reason", reason);
			r.put("sdp", sdp);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

