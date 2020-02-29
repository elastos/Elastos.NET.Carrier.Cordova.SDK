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

  import android.util.Base64;
  import org.apache.cordova.CallbackContext;
  import org.apache.cordova.PluginResult;
  import org.elastos.carrier.filetransfer.FileTransfer;
  import org.elastos.carrier.filetransfer.FileTransferInfo;
  import org.elastos.carrier.session.PortForwardingProtocol;
  import org.elastos.carrier.session.Session;
  import org.elastos.trinity.runtime.TrinityPlugin;
  import org.json.JSONArray;
  import org.json.JSONException;
  import org.json.JSONObject;
  import java.nio.charset.Charset;
  import java.util.List;
  import java.util.Map;
  import java.util.HashMap;
  import java.util.Objects;
  import java.util.UUID;
  import org.elastos.carrier.*;
  import org.elastos.carrier.exceptions.CarrierException;


  /**
   * This class echoes a string called from JavaScript.
   */
  public class CarrierPlugin extends TrinityPlugin {
      private static String TAG = "CarrierPlugin";

      private static final int OK = 0;
      private static final int CARRIER = 1;
      private static final int SESSION = 2;
      private static final int STREAM = 3;
      private static final int FRIEND_INVITE = 4;
      private static final int GROUP = 5;
      private static final int FILE_TRANSFER = 6;

      private static final String SUCCESS = "Success!";
      private static final String INVALID_ID = "Id invalid!";

      private Map<Integer, PluginCarrierHandler> mCarrierMap;
      private HashMap<Integer, Session> mSessionMap;
      private HashMap<Integer, PluginStreamHandler> mStreamMap;
      private HashMap<String, Group> mGroupMap;
      private HashMap<Integer, PluginFileTransferHandler> mFileTransferHandlerMap;

      private CallbackContext mCarrierCallbackContext = null;
      private CallbackContext mSessionCallbackContext = null;
      private CallbackContext mStreamCallbackContext = null;
      private CallbackContext mFIRCallbackContext = null;
      private CallbackContext mGroupCallbackContext = null;
      private CallbackContext mFileTransferCallbackContext = null;

      public CarrierPlugin() {
          mCarrierMap = new HashMap();
          mSessionMap = new HashMap();
          mStreamMap = new HashMap();
          mGroupMap = new HashMap<>();
          mFileTransferHandlerMap = new HashMap<>();
      }

      /**
       * The final call you receive before your activity is destroyed.
       */
      @Override
      public void onDestroy() {
          for (int key : mCarrierMap.keySet()){
              PluginCarrierHandler carrierHandler = mCarrierMap.get(key);
              if (carrierHandler != null) {
                  carrierHandler.mCarrier.kill();
              }
          }
          clearGroupHandlerMap();
          super.onDestroy();
      }

      @Override
      public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
          try {
              switch (action) {
                  case "test":
                      this.test(args, callbackContext);
                      break;
                  case "setListener":
                      this.setListener(args, callbackContext);
                      break;
                  case "createObject":
                      this.createObject(args, callbackContext);
                      break;
                  case "carrierStart":
                      this.carrierStart(args, callbackContext);
                      break;
                  case "isReady":
                      this.isReady(args, callbackContext);
                      break;
                  case "acceptFriend":
                      this.acceptFriend(args, callbackContext);
                      break;
                  case "addFriend":
                      this.addFriend(args, callbackContext);
                      break;
                  case "getFriend":
                      this.getFriend(args, callbackContext);
                      break;
                  case "labelFriend":
                      this.labelFriend(args, callbackContext);
                      break;
                  case "isFriend":
                      this.isFriend(args, callbackContext);
                      break;
                  case "removeFriend":
                      this.removeFriend(args, callbackContext);
                      break;
                  case "getFriends":
                      this.getFriends(args, callbackContext);
                      break;
                  case "sendFriendMessage":
                      this.sendFriendMessage(args, callbackContext);
                      break;
                  case "getSelfInfo":
                      this.getSelfInfo(args, callbackContext);
                      break;
                  case "setSelfInfo":
                      this.setSelfInfo(args, callbackContext);
                      break;
                  case "getNospam":
                      this.getNospam(args, callbackContext);
                      break;
                  case "setNospam":
                      this.setNospam(args, callbackContext);
                      break;
                  case "getPresence":
                      this.getPresence(args, callbackContext);
                      break;
                  case "setPresence":
                      this.setPresence(args, callbackContext);
                      break;
                  case "inviteFriend":
                      this.inviteFriend(args, callbackContext);
                      break;
                  case "replyFriendInvite":
                      this.replyFriendInvite(args, callbackContext);
                      break;
                  case "destroy":
                      this.destroy(args, callbackContext);
                      break;
                  case "newSession":
                      this.newSession(args, callbackContext);
                      break;
                  case "sessionClose":
                      this.sessionClose(args, callbackContext);
                      break;
                  case "getPeer":
                      this.getPeer(args, callbackContext);
                      break;
                  case "sessionRequest":
                      this.sessionRequest(args, callbackContext);
                      break;
                  case "sessionReplyRequest":
                      this.sessionReplyRequest(args, callbackContext);
                      break;
                  case "sessionStart":
                      this.sessionStart(args, callbackContext);
                  case "addStream":
                      this.addStream(args, callbackContext);
                      break;
                  case "removeStream":
                      this.removeStream(args, callbackContext);
                      break;
                  case "addService":
                      this.addService(args, callbackContext);
                      break;
                  case "removeService":
                      this.removeService(args, callbackContext);
                      break;
                  case "getTransportInfo":
                      this.getTransportInfo(args, callbackContext);
                      break;
                  case "streamWrite":
                      this.streamWrite(args, callbackContext);
                      break;
                  case "openChannel":
                      this.openChannel(args, callbackContext);
                      break;
                  case "closeChannel":
                      this.closeChannel(args, callbackContext);
                      break;
                  case "writeChannel":
                      this.writeChannel(args, callbackContext);
                      break;
                  case "pendChannel":
                      this.pendChannel(args, callbackContext);
                      break;
                  case "resumeChannel":
                      this.resumeChannel(args, callbackContext);
                      break;
                  case "openPortForwarding":
                      this.openPortForwarding(args, callbackContext);
                      break;
                  case "closePortForwarding":
                      this.closePortForwarding(args, callbackContext);
                      break;
                  //static
                  case "getVersion":
                      this.getVersion(callbackContext);
                      break;
                  case "getIdFromAddress":
                      this.getIdFromAddress(args, callbackContext);
                      break;
                  case "isValidAddress":
                      this.isValidAddress(args, callbackContext);
                      break;
                  case "isValidId":
                      this.isValidId(args, callbackContext);
                      break;
                  case "createGroup":
                      this.createGroup(args, callbackContext);
                      break;
                  case "joinGroup":
                      this.joinGroup(args, callbackContext);
                      break;
                  case "inviteGroup":
                      this.inviteGroup(args, callbackContext);
                      break;
                  case "leaveGroup":
                      this.leaveGroup(args, callbackContext);
                      break;
                  case "sendGroupMessage":
                      this.sendGroupMessage(args, callbackContext);
                      break;
                  case "getGroupTitle":
                      this.getGroupTitle(args, callbackContext);
                      break;
                  case "setGroupTitle":
                      this.setGroupTitle(args, callbackContext);
                      break;
                  case "getGroupPeers":
                      this.getGroupPeers(args, callbackContext);
                      break;
                  case "getGroupPeer":
                      this.getGroupPeer(args, callbackContext);
                      break;
                  case "generateFileTransFileId":
                      this.generateFileTransFileId(args, callbackContext);
                      break;
                  case "closeFileTrans":
                      this.closeFileTrans(args, callbackContext);
                      break;
                  case "getFileTransFileId":
                      this.getFileTransFileId(args, callbackContext);
                      break;
                  case "getFileTransFileName":
                      this.getFileTransFileName(args, callbackContext);
                      break;
                  case "fileTransConnect":
                      this.fileTransConnect(args, callbackContext);
                      break;
                  case "acceptFileTransConnect":
                      this.acceptFileTransConnect(args, callbackContext);
                      break;
                  case "addFileTransFile":
                      this.addFileTransFile(args, callbackContext);
                      break;
                  case "pullFileTransData":
                      this.pullFileTransData(args, callbackContext);
                      break;
                  case "writeFileTransData":
                      this.writeFileTransData(args, callbackContext);
                      break;
                  case "sendFileTransFinish":
                      this.sendFileTransFinish(args, callbackContext);
                      break;
                  case "cancelFileTrans":
                      this.cancelFileTrans(args, callbackContext);
                      break;
                  case "pendFileTrans":
                      this.pendFileTrans(args, callbackContext);
                      break;
                  case "resumeFileTrans":
                      this.resumeFileTrans(args, callbackContext);
                      break;
                  case "newFileTransfer":
                      this.newFileTransfer(args, callbackContext);
                      break;
                  default:
                      return false;
              }
          } catch (CarrierException e) {
              String error = String.format("%s error (0x%x)", action, e.getErrorCode());
              // String error = e.getLocalizedMessage();
              callbackContext.error(error);
          }
          return true;
      }

      private void test(JSONArray args, CallbackContext callbackContext) throws JSONException {
          String data = args.getString(0);
          byte[] rawData = Base64.decode(data, Base64.DEFAULT);
      }

      private void getVersion(CallbackContext callbackContext) {
          String version = Carrier.getVersion();
          callbackContext.success(version);
      }

      private void getIdFromAddress(JSONArray args, CallbackContext callbackContext) throws JSONException {
          String address = args.getString(0);
          if (address != null && address.length() > 0) {
              String usrId = Carrier.getIdFromAddress(address);
              callbackContext.success(usrId);
          } else {
              callbackContext.error("Expected one non-empty string argument.");
          }
      }

      private void isValidAddress(JSONArray args, CallbackContext callbackContext) throws JSONException {
          String address = args.getString(0);
          if (address != null && address.length() > 0) {
              Boolean ret = Carrier.isValidAddress(address);
              callbackContext.success(ret.toString());
          } else {
              callbackContext.error("Expected one non-empty string argument.");
          }
      }

      private void isValidId(JSONArray args, CallbackContext callbackContext) throws JSONException {
          String userId = args.getString(0);
          if (userId != null && userId.length() > 0) {
              Boolean ret = Carrier.isValidId(userId);
              callbackContext.success(ret.toString());
          } else {
              callbackContext.error("Expected one non-empty string argument.");
          }
      }

      private void setListener(JSONArray args, CallbackContext callbackContext) throws JSONException {
          Integer type = args.getInt(0);
          switch (type) {
              case CARRIER:
                  mCarrierCallbackContext = callbackContext;
                  break;
              case SESSION:
                  mSessionCallbackContext = callbackContext;
                  break;
              case STREAM:
                  mStreamCallbackContext = callbackContext;
                  break;
              case FRIEND_INVITE:
                  mFIRCallbackContext = callbackContext;
                  break;
              case GROUP:
                  mGroupCallbackContext = callbackContext;
                  break;
              case FILE_TRANSFER:
                  mFileTransferCallbackContext = callbackContext;
                  break;

          }
          //         Don't return any result now
          PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
          pluginResult.setKeepCallback(true);
          callbackContext.sendPluginResult(pluginResult);
      }

      private void createObject(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          String dir = args.getString(0);
          String config = args.getString(1);

          dir = cordova.getActivity().getFilesDir() + "/data/carrier/" + dir;

          PluginCarrierHandler carrierHandler = PluginCarrierHandler.createInstance(dir, config,
                  mCarrierCallbackContext, mGroupCallbackContext, this);

          if (carrierHandler != null) {
              mCarrierMap.put(carrierHandler.mCode, carrierHandler);

              JSONObject r = new JSONObject();
              r.put("id", carrierHandler.mCode);
              UserInfo selfInfo = carrierHandler.mCarrier.getSelfInfo();
              r.put("nodeId", carrierHandler.mCarrier.getNodeId());
              r.put("userId", selfInfo.getUserId());
              r.put("address", carrierHandler.mCarrier.getAddress());
              r.put("nospam", carrierHandler.mCarrier.getNospam());
              r.put("presence", carrierHandler.mCarrier.getPresence().value());
              callbackContext.success(r);
          } else {
              callbackContext.error("error");
          }
      }

      private void carrierStart(JSONArray args, CallbackContext callbackContext) throws JSONException {
          Integer id = args.getInt(0);
          Integer iterateInterval = args.getInt(1);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.start(iterateInterval);
              callbackContext.success("ok");
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getSelfInfo(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              UserInfo selfInfo = carrierHandler.mCarrier.getSelfInfo();
              JSONObject r = carrierHandler.getUserInfoJson(selfInfo);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void setSelfInfo(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String name = args.getString(1);
          String value = args.getString(2);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              UserInfo selfInfo = carrierHandler.mCarrier.getSelfInfo();

              switch (name) {
                  case "name":
                      selfInfo.setName(value);
                      break;
                  case "description":
                      selfInfo.setDescription(value);
                      break;
                  case "gender":
                      selfInfo.setGender(value);
                      break;
                  case "phone":
                      selfInfo.setPhone(value);
                      break;
                  case "email":
                      selfInfo.setEmail(value);
                      break;
                  case "region":
                      selfInfo.setRegion(value);
                      break;
                  case "hasAvatar":
                      if (value.equals("ture")) selfInfo.setHasAvatar(true);
                      else selfInfo.setHasAvatar(false);
                      break;
                  default:
                      callbackContext.error("Name invalid!");
                      return;
              }

              carrierHandler.mCarrier.setSelfInfo(selfInfo);

              JSONObject r = new JSONObject();
              r.put("name", name);
              r.put("value", value);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getNospam(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              JSONObject r = new JSONObject();
              r.put("nospam", carrierHandler.mCarrier.getNospam());
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void setNospam(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int nospam = args.getInt(1);

          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.setNospam(nospam);
              JSONObject r = new JSONObject();
              r.put("nospam", nospam);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getPresence(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              JSONObject r = new JSONObject();
              r.put("presence", carrierHandler.mCarrier.getPresence().value());
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void setPresence(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int presence = args.getInt(1);

          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.setPresence(PresenceStatus.valueOf(presence));
              JSONObject r = new JSONObject();
              r.put("presence", presence);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void isReady(JSONArray args, CallbackContext callbackContext) throws JSONException {
          Integer id = args.getInt(0);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              JSONObject r = new JSONObject();
              r.put("isReady", carrierHandler.mCarrier.isReady());
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getFriends(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              List<FriendInfo> friends = carrierHandler.mCarrier.getFriends();
              JSONObject r = new JSONObject();
              r.put("friends", carrierHandler.getFriendsInfoJson(friends));
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getFriend(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String userId = args.getString(1);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              FriendInfo info = carrierHandler.mCarrier.getFriend(userId);
              JSONObject r = carrierHandler.getFriendInfoJson(info);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void labelFriend(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String userId = args.getString(1);
          String label = args.getString(2);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.labelFriend(userId, label);
              JSONObject r = new JSONObject();
              r.put("userId", userId);
              r.put("label", label);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void isFriend(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String userId = args.getString(1);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              JSONObject r = new JSONObject();
              r.put("userId", userId);
              r.put("isFriend", carrierHandler.mCarrier.isFriend(userId));
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void acceptFriend(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String userId = args.getString(1);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.acceptFriend(userId);
              JSONObject r = new JSONObject();
              r.put("userId", userId);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void addFriend(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String address = args.getString(1);
          String hello = args.getString(2);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.addFriend(address, hello);
              JSONObject r = new JSONObject();
              r.put("address", address);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void removeFriend(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String userId = args.getString(1);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.removeFriend(userId);
              JSONObject r = new JSONObject();
              r.put("userId", userId);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void sendFriendMessage(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String to = args.getString(1);
          String message = args.getString(2);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              boolean isOffline = carrierHandler.mCarrier.sendFriendMessage(to, message);
              JSONObject r = new JSONObject();
              r.put("isOffline", isOffline);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void inviteFriend(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String to = args.getString(1);
          String data = args.getString(2);
          int handlerId = args.getInt(3);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              FIRHandler handler = new FIRHandler(handlerId, mFIRCallbackContext);
              carrierHandler.mCarrier.inviteFriend(to, data, handler);
              JSONObject r = new JSONObject();
              r.put("to", to);
              r.put("data", data);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void replyFriendInvite(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String to = args.getString(1);
          int status = args.getInt(2);
          String reason = args.getString(3);
          String data = args.getString(4);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.replyFriendInvite(to, status, reason, data);
              JSONObject r = new JSONObject();
              r.put("to", to);
              r.put("status", status);
              r.put("reason", reason);
              r.put("data", data);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void destroy(JSONArray args, CallbackContext callbackContext) throws JSONException {
          Integer id = args.getInt(0);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              carrierHandler.mCarrier.kill();
              clearGroupHandlerMap();
              JSONObject r = new JSONObject();
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void newSession(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String to = args.getString(1);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {
              Session session = carrierHandler.mSessionManager.newSession(to);
              if (session != null) {
                  Integer code = System.identityHashCode(session);
                  mSessionMap.put(code, session);
                  JSONObject r = new JSONObject();
                  r.put("id", code);
                  r.put("peer", session.getPeer());
                  callbackContext.success(r);
              } else {
                  callbackContext.error("error");
              }
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void sessionClose(JSONArray args, CallbackContext callbackContext) throws JSONException {
          Integer id = args.getInt(0);
          Session session = mSessionMap.get(id);
          if (session != null) {
              session.close();
              callbackContext.success();
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getPeer(JSONArray args, CallbackContext callbackContext) throws JSONException {
          Integer id = args.getInt(0);
          Session session = mSessionMap.get(id);
          if (session != null) {
              String peer = session.getPeer();
              JSONObject r = new JSONObject();
              r.put("peer", peer);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void sessionRequest(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int handlerId = args.getInt(1);
          Session session = mSessionMap.get(id);
          if (session != null) {
              SRCHandler handler = new SRCHandler(handlerId, mSessionCallbackContext);
              session.request(handler);
              callbackContext.success();
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void sessionReplyRequest(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int status = args.getInt(1);
          String reason = null;
          if (status != 0) {
              reason = args.getString(2);
          }
          Session session = mSessionMap.get(id);
          if (session != null) {
              session.replyRequest(status, reason);
              JSONObject r = new JSONObject();
              r.put("status", status);
              r.put("reason", reason);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void sessionStart(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String sdp = args.getString(1);
          Session session = mSessionMap.get(id);
          if (session != null) {
              session.start(sdp);
              JSONObject r = new JSONObject();
              r.put("sdp", sdp);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void addStream(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int type = args.getInt(1);
          int options = args.getInt(2);

          Session session = mSessionMap.get(id);
          if (session != null) {
              PluginStreamHandler streamHandler = PluginStreamHandler.createInstance(session, type, options, mStreamCallbackContext);
              if (streamHandler != null) {
                  mStreamMap.put(streamHandler.mCode, streamHandler);
                  JSONObject r = new JSONObject();
                  r.put("objId", streamHandler.mCode);
                  r.put("id", streamHandler.mStream.getStreamId());
                  r.put("type", type);
                  r.put("options", options);
                  callbackContext.success(r);
              } else {
                  callbackContext.error("error");
              }
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void removeStream(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          Integer streamId = args.getInt(1);

          Session session = mSessionMap.get(id);
          PluginStreamHandler streamHandler = mStreamMap.get(streamId);
          if (session != null && streamHandler != null) {
              session.removeStream(streamHandler.mStream);
              callbackContext.success();
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void addService(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String service = args.getString(1);
          int protocol = args.getInt(2);
          String host = args.getString(3);
          String port = args.getString(4);

          Session session = mSessionMap.get(id);
          if (session != null) {
              session.addService(service, PortForwardingProtocol.valueOf(protocol), host, port);
              JSONObject r = new JSONObject();
              r.put("service", service);
              r.put("protocol", protocol);
              r.put("host", host);
              r.put("port", port);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void removeService(JSONArray args, CallbackContext callbackContext) throws JSONException {
          Integer id = args.getInt(0);
          String service = args.getString(1);

          Session session = mSessionMap.get(id);
          if (session != null) {
              session.removeService(service);
              JSONObject r = new JSONObject();
              r.put("service", service);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getTransportInfo(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              JSONObject r = streamHandler.getTransportInfoJson();
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void streamWrite(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String data = args.getString(1);
          byte[] rawData = Base64.decode(data, Base64.DEFAULT);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              int written = streamHandler.mStream.writeData(rawData);
              JSONObject r = new JSONObject();
              r.put("written", written);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void openChannel(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String cookie = args.getString(1);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              int channel = streamHandler.mStream.openChannel(cookie);
              JSONObject r = new JSONObject();
              r.put("channel", channel);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void closeChannel(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int channel = args.getInt(1);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              streamHandler.mStream.closeChannel(channel);
              JSONObject r = new JSONObject();
              r.put("channel", channel);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void writeChannel(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int channel = args.getInt(1);
          String data = args.getString(2);
          byte[] rawData = Base64.decode(data, Base64.DEFAULT);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              int written = streamHandler.mStream.writeData(channel, rawData);
              JSONObject r = new JSONObject();
              r.put("channel", channel);
              r.put("written", written);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void pendChannel(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int channel = args.getInt(1);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              streamHandler.mStream.pendChannel(channel);
              JSONObject r = new JSONObject();
              r.put("channel", channel);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void resumeChannel(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int channel = args.getInt(1);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              streamHandler.mStream.resumeChannel(channel);
              JSONObject r = new JSONObject();
              r.put("channel", channel);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void openPortForwarding(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String service = args.getString(1);
          int protocol = args.getInt(2);
          String host = args.getString(3);
          String port = args.getString(4);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              int pfId = streamHandler.mStream.openPortForwarding(service, PortForwardingProtocol.valueOf(protocol), host, port);
              JSONObject r = new JSONObject();
              r.put("pfId", pfId);
              r.put("service", service);
              r.put("protocol", protocol);
              r.put("host", host);
              r.put("port", port);
              callbackContext.success(r);
          } else {
              callbackContext.error("error");
          }
      }

      private void closePortForwarding(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          int pfId = args.getInt(1);

          PluginStreamHandler streamHandler = mStreamMap.get(id);
          if (streamHandler != null) {
              streamHandler.mStream.closePortForwarding(pfId);
              JSONObject r = new JSONObject();
              r.put("pfId", pfId);
              callbackContext.success(r);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void createGroup(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int id = args.getInt(0);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);

          if (carrierHandler != null) {
              String groupId = getGroupId();
              Group group = carrierHandler.mCarrier.newGroup();
              group.setTitle("Untitled");

              addGroupMap(groupId, group);
              carrierHandler.groups.put(group, groupId);

              JSONObject jsonObject = new JSONObject();
              jsonObject.put("groupId", groupId);

              callbackContext.success(jsonObject);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void joinGroup(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int id = args.getInt(0);
          String friendId = args.getString(1);
          String cookieBase58 = args.getString(2);

          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          byte[] cookie = Base58.decode(cookieBase58);
          if (carrierHandler != null) {
              String groupId = getGroupId();

              Group group = carrierHandler.mCarrier.groupJoin(friendId, cookie);

              addGroupMap(groupId, group);
              carrierHandler.groups.put(group, groupId);

              JSONObject jsonObject = new JSONObject();
              jsonObject.put("groupId", groupId);

              callbackContext.success(jsonObject);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void inviteGroup(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          String groupId = args.getString(0);
          String friendId = args.getString(1);

          Group group = null;
          try {
              group = Objects.requireNonNull(mGroupMap.get(groupId));
          } catch (NullPointerException e) {
          }
          if (group != null) {
              group.invite(friendId);
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void leaveGroup(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int id = args.getInt(0);
          String groupId = args.getString(1);

          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          Group group = null;
          try {
              group = Objects.requireNonNull(mGroupMap.get(groupId));
          } catch (NullPointerException e) {
          }
          if (carrierHandler != null && group != null) {
              carrierHandler.mCarrier.groupLeave(group);
              deleteGroupHandlerFromMap(groupId);
              carrierHandler.groups.remove(group);

              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void sendGroupMessage(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          String groupId = args.getString(0);
          String messageData = args.getString(1);

          Group group = null;
          try {
              group = Objects.requireNonNull(mGroupMap.get(groupId));
          } catch (NullPointerException e) {
          }
          byte[] message = messageData.getBytes(Charset.forName("UTF-8"));

          if (group != null) {
              group.sendMessage(message);
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getGroupTitle(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          String groupId = args.getString(0);
          Group group = null;
          try {
              group = Objects.requireNonNull(mGroupMap.get(groupId));
          } catch (NullPointerException e) {
          }
          if (group != null) {
              callbackContext.success(getGroupTitleJson(group));
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void setGroupTitle(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          String groupId = args.getString(0);
          String groupTitle = args.getString(1);

          Group group = null;
          try {
              group = Objects.requireNonNull(mGroupMap.get(groupId));
          } catch (NullPointerException e) {
          }
          if (group != null) {
              group.setTitle(groupTitle);
              callbackContext.success(getGroupTitleJson(group));
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getGroupPeers(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          String groupId = args.getString(0);

          Group group = null;
          try {
              group = Objects.requireNonNull(mGroupMap.get(groupId));
          } catch (NullPointerException e) {
          }
          if (group != null) {
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("peers", getGroupPeersInfoJson(group));
              callbackContext.success(jsonObject);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getGroupPeer(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          String groupId = args.getString(0);
          String peerId = args.getString(1);

          Group group = null;
          try {
              group = Objects.requireNonNull(mGroupMap.get(groupId));
          } catch (NullPointerException e) {
          }
          if (group != null && peerId != null) {
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("peer", getGroupPeerInfoJson(group, peerId));
              callbackContext.success(jsonObject);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void clearGroupHandlerMap() {
          mGroupMap.clear();
      }

      private void deleteGroupHandlerFromMap(String groupHandlerId) {
          mGroupMap.remove(groupHandlerId);
      }

      private String getGroupId() {
          //TODO tobe modify , If can get groupid
          return randomUUID();
      }

      private String randomUUID() {
          return UUID.randomUUID().toString().replace("-", "");
      }

      private void addGroupMap(String groupId, Group group) {
          mGroupMap.put(groupId, group);
      }

      private JSONObject getGroupPeersInfoJson(Group group) throws JSONException, CarrierException {
          JSONObject jsonObject = new JSONObject();

          List<Group.PeerInfo> peerInfos = group.getPeers();
          for (Group.PeerInfo peerInfo : peerInfos) {
              JSONObject peerObj = new JSONObject();
              peerObj.put("peerName", peerInfo.getName());
              peerObj.put("peerUserId", peerInfo.getUserId());

              jsonObject.put(peerInfo.getUserId(), peerObj);
          }
          return jsonObject;
      }

      private JSONObject getGroupPeerInfoJson(Group group, String peerId) throws JSONException, CarrierException {
          JSONObject peerObj = new JSONObject();

          Group.PeerInfo peerInfo = group.getPeer(peerId);
          peerObj.put("peerName", peerInfo.getName());
          peerObj.put("peerUserId", peerInfo.getUserId());

          return peerObj;
      }

      private JSONObject getGroupTitleJson(Group group) throws JSONException, CarrierException {
          JSONObject jsonObject = new JSONObject();
          jsonObject.put("groupTitle", group.getTitle());
          return jsonObject;
      }



      private void closeFileTrans(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.close();
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getFileTransFileId(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String filename = args.getString(1);
          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {

              String fileId = fileTransfer.getFileId(filename);
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("fileId",fileId);
              callbackContext.success(jsonObject);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void getFileTransFileName(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String fileId = args.getString(1);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }

          if (fileTransfer != null) {
              String filename = fileTransfer.getFileName(fileId);
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("filename",filename);
              callbackContext.success(jsonObject);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }


      private void fileTransConnect(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.connect();
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void acceptFileTransConnect(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.acceptConnect();
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void addFileTransFile(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          JSONObject fileinfo = args.getJSONObject(1);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.addFile(decodeFileTransferInfo(fileinfo));
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void pullFileTransData(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String fileId = args.getString(1);
          long offset = args.getLong(2);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.pullData(fileId,offset);
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void writeFileTransData(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String fileId = args.getString(1);
          String data = args.getString(2);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.writeData(fileId,data.getBytes());
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void sendFileTransFinish(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String fileId = args.getString(1);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.sendFinish(fileId);
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void cancelFileTrans(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String fileId = args.getString(1);
          int status = args.getInt(2);
          String reason = args.getString(3);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.cancelTransfer(fileId,status,reason);
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void pendFileTrans(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String fileId = args.getString(1);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.pendTransfer(fileId);
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void resumeFileTrans(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          int fileTransferId = args.getInt(0);
          String fileId = args.getString(1);

          FileTransfer fileTransfer = null;
          try {
              fileTransfer = Objects.requireNonNull(mFileTransferHandlerMap.get(fileTransferId)).getmFileTransfer();
          } catch (NullPointerException e) {
          }
          if (fileTransfer != null) {
              fileTransfer.resumeTransfer(fileId);
              callbackContext.success(SUCCESS);
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void newFileTransfer(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          Integer id = args.getInt(0);
          String to = args.getString(1);
          JSONObject fileInfo = args.getJSONObject(2);
          PluginCarrierHandler carrierHandler = mCarrierMap.get(id);
          if (carrierHandler != null) {

              PluginFileTransferHandler pluginFileTransferHandler = new PluginFileTransferHandler(mFileTransferCallbackContext);
              FileTransfer fileTransfer = carrierHandler.getFileTransferManager()
                      .newFileTransfer(to,decodeFileTransferInfo(fileInfo),pluginFileTransferHandler);

              if (fileTransfer != null) {
                  Integer code = System.identityHashCode(fileTransfer);
                  pluginFileTransferHandler.setmFileTransfer(fileTransfer);
                  pluginFileTransferHandler.setFileTransferId(code);

                  mFileTransferHandlerMap.put(code, pluginFileTransferHandler);
                  JSONObject r = new JSONObject();
                  r.put("fileTransferId", code);
                  callbackContext.success(r);
              } else {
                  callbackContext.error("error");
              }
          } else {
              callbackContext.error(INVALID_ID);
          }
      }

      private void generateFileTransFileId(JSONArray args, CallbackContext callbackContext) throws JSONException, CarrierException {
          JSONObject jsonObject = new JSONObject();
          jsonObject.put("fileId",FileTransfer.generateFileId());
          callbackContext.success(jsonObject);
      }

      private FileTransferInfo decodeFileTransferInfo(JSONObject jsonObject) throws JSONException {
          String filename = jsonObject.getString("filename");
          String fileId = jsonObject.getString("fileId");
          long size = jsonObject.getLong("size");
          return new FileTransferInfo(filename,fileId,size);
      }
  }
