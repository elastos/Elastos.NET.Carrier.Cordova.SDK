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
 import org.elastos.carrier.filetransfer.FileTransfer;
 import org.elastos.carrier.filetransfer.FileTransferHandler;
 import org.elastos.carrier.filetransfer.FileTransferState;
 import org.json.JSONException;
 import org.json.JSONObject;

 import java.nio.charset.StandardCharsets;

 public class PluginFileTransferHandler implements FileTransferHandler {
     private static String TAG = "PluginFileTransferHandler";
     private CallbackContext mCallbackContext;
     private int fileTransferId;
     private FileTransfer mFileTransfer ;

     PluginFileTransferHandler(CallbackContext callbackContext) {
         this.mCallbackContext = callbackContext;
     }

     int getFileTransferId() {
         return fileTransferId;
     }

     void setFileTransferId(int fileTransferId) {
         this.fileTransferId = fileTransferId;
     }

     FileTransfer getmFileTransfer() {
         return mFileTransfer;
     }

     void setmFileTransfer(FileTransfer mFileTransfer) {
         this.mFileTransfer = mFileTransfer;
     }

     private void sendEvent(JSONObject info) throws JSONException {
         info.put("fileTransferId", fileTransferId);
         if (mCallbackContext != null) {
             PluginResult result = new PluginResult(PluginResult.Status.OK, info);
             result.setKeepCallback(true);
             mCallbackContext.sendPluginResult(result);
         }
     }

     @Override
     public void onStateChanged(FileTransfer filetransfer, FileTransferState state) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onStateChanged");
             r.put("state", state);
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

     @Override
     public void onFileRequest(FileTransfer filetransfer, String fileId, String filename, long size) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onFileRequest");
             r.put("fileId", fileId);
             r.put("filename", filename);
             r.put("size", size);
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

     @Override
     public void onPullRequest(FileTransfer filetransfer, String fileId, long offset) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onPullRequest");
             r.put("fileId", fileId);
             r.put("offset", offset);
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

     @Override
     public boolean onData(FileTransfer filetransfer, String fileId, byte[] data) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onData");
             r.put("fileId", fileId);
             r.put("data", new String(data, StandardCharsets.UTF_8));
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
         return true;
     }

     @Override
     public void onDataFinished(FileTransfer filetransfer, String fileId) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onDataFinished");
             r.put("fileId", fileId);
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

     @Override
     public void onPending(FileTransfer filetransfer, String fileId) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onPending");
             r.put("fileId", fileId);
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

     @Override
     public void onResume(FileTransfer filetransfer, String fileId) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onResume");
             r.put("fileId", fileId);
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

     @Override
     public void onCancel(FileTransfer filetransfer, String fileId, int status, String reason) {
         JSONObject r = new JSONObject();
         try {
             r.put("name", "onCancel");
             r.put("fileId", fileId);
             r.put("status", status);
             r.put("reason", reason);
             sendEvent(r);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }
 }