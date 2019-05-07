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

import Foundation
import ElastosCarrierSDK

typealias Carrier = ElastosCarrierSDK.Carrier
typealias Session = ElastosCarrierSDK.CarrierSession


@objc(CarrierPlugin)
class CarrierPlugin : TrinityPlugin {

    //        let test = Test()

    let OK = 0;
    var CARRIER = 1;
    var SESSION = 2;
    var STREAM = 3;
    var FRIEND_INVITE = 4;

    var mCarrierDict = [Int: PluginCarrierHandler]()
    var mSessionDict = [Int: Session]()
    var mStreamDict = [Int: PluginStreamHandler]()

    var carrierCallbackId: String = ""
    var sessionCallbackId: String = ""
    var streamCallbackId: String = ""
    var FIRCallbackId: String = ""

    var count: Int = 1;

    //    override init() {
    //        super.init();
    //    }

    @objc func initVal(_ command: CDVInvokedUrlCommand) {

    }

    @objc func success(_ command: CDVInvokedUrlCommand, retAsString: String) {
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: retAsString);

        self.commandDelegate.send(result, callbackId: command.callbackId)
    }

    @objc func success(_ command: CDVInvokedUrlCommand, retAsDict: NSDictionary) {
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: (retAsDict as! [AnyHashable : Any]));

        self.commandDelegate.send(result, callbackId: command.callbackId)
    }

    @objc func error(_ command: CDVInvokedUrlCommand, retAsString: String) {
        let result = CDVPluginResult(status: CDVCommandStatus_ERROR,
                                     messageAs: retAsString);

        self.commandDelegate.send(result, callbackId: command.callbackId)
    }

    @objc func test(_ command: CDVInvokedUrlCommand) {

    }

    @objc func getVersion(_ command: CDVInvokedUrlCommand) {
        let version = ElastosCarrierSDK.Carrier.getVersion()
        self.success(command, retAsString: version);
    }

    @objc func getIdFromAddress(_ command: CDVInvokedUrlCommand) {
        let address = command.arguments[0] as? String ?? ""
        if (!address.isEmpty) {
            let usrId = ElastosCarrierSDK.Carrier.getUserIdFromAddress(address);
            self.success(command, retAsString: usrId!);
        }
        else {
            self.error(command, retAsString: "Expected one non-empty let argument.");
        }
    }

    @objc func isValidAddress(_ command: CDVInvokedUrlCommand) {
        let address = command.arguments[0] as? String ?? ""
        if (!address.isEmpty) {
            let ret = Carrier.isValidAddress(address);
            self.success(command, retAsString: String(ret));
        }
        else {
            self.error(command, retAsString: "Expected one non-empty let argument.");
        }
    }

    @objc func isValidId(_ command: CDVInvokedUrlCommand) {
        let userId = command.arguments[0] as? String ?? ""
        if (!userId.isEmpty) {
            let ret = ElastosCarrierSDK.Carrier.isValidUserId(userId);
            self.success(command, retAsString: String(ret));
        }
        else {
            self.error(command, retAsString: "Expected one non-empty let argument.");
        }
    }

    @objc func setListener(_ command: CDVInvokedUrlCommand) {
        let type = command.arguments[0] as? Int ?? 0

        switch (type) {
        case CARRIER:
            carrierCallbackId = command.callbackId;
        case SESSION:
            sessionCallbackId = command.callbackId;
        case STREAM:
            streamCallbackId = command.callbackId;
        case FRIEND_INVITE:
            FIRCallbackId = command.callbackId;
        default:
            self.error(command, retAsString: "Expected one non-empty let argument.");
        }


        //         Don't return any result now
        let result = CDVPluginResult(status: CDVCommandStatus_NO_RESULT);
        result?.setKeepCallbackAs(true);
        self.commandDelegate.send(result, callbackId: command.callbackId)
    }

    @objc func createObject(_ command: CDVInvokedUrlCommand) {
        let dir = command.arguments[0] as? String ?? ""
        let config = command.arguments[1] as? String ?? ""

        do {
        let carrierHandler = try PluginCarrierHandler.createInstance(dir, config, carrierCallbackId, self.commandDelegate);

            count += 1;
            carrierHandler.mCode = count;
            mCarrierDict[count] = carrierHandler;

            let selfInfo: UserInfo = try carrierHandler.mCarrier.getSelfUserInfo();
            let ret: NSDictionary = [
                "id": carrierHandler.mCode,
                "nodeId" : carrierHandler.mCarrier.getNodeId(),
                "userId" : selfInfo.userId ?? "",
                "address": carrierHandler.mCarrier.getAddress(),
                "nospam" : try carrierHandler.mCarrier.getSelfNospam(),
                "presence" : try carrierHandler.mCarrier.getSelfPresence().rawValue,
                ]

            self.success(command, retAsDict: ret);
        }
        catch {
            self.error(command, retAsString: error.localizedDescription);
        }
    }

    @objc func carrierStart(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let iterateleterval = command.arguments[1] as? Int ?? 0

        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.start(iterateInterval: iterateleterval);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
            self.success(command, retAsString: "ok");
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func getSelfInfo(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let selfInfo: UserInfo = try carrierHandler.mCarrier.getSelfUserInfo();
                let ret: NSDictionary = carrierHandler.getUserInfoDict(selfInfo);
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func setSelfInfo(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let name = command.arguments[1] as? String ?? ""
        let value = command.arguments[2] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let selfInfo: UserInfo = try carrierHandler.mCarrier.getSelfUserInfo();

                switch (name) {
                case "name":
                    selfInfo.name = value;
                case "description":
                    selfInfo.briefDescription = value;
                case "gender":
                    selfInfo.gender = value;
                case "phone":
                    selfInfo.phone = value;
                case "email":
                    selfInfo.email = value;
                case "region":
                    selfInfo.region = value;
                case "hasAvatar":
                    selfInfo.hasAvatar = (value == "ture");
                default:
                    self.error(command, retAsString: "Name invalid!");
                    return;
                }

                try carrierHandler.mCarrier.setSelfUserInfo(selfInfo);

                let ret: NSDictionary = [
                    "name": name,
                    "value": value,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func getNospam(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let ret: NSDictionary = [
                    "nospam": try carrierHandler.mCarrier.getSelfNospam(),
                    ]

                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func setNospam(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let nospam = command.arguments[1] as? UInt32 ?? 0

        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.setSelfNospam(nospam);
                let ret: NSDictionary = [
                    "nospam": nospam,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func getPresence(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let presence = try carrierHandler.mCarrier.getSelfPresence().rawValue
                let ret: NSDictionary = [
                    "presence": presence,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func setPresence(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let presence = command.arguments[1] as? Int ?? 0

        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.setSelfPresence(CarrierPresenceStatus(rawValue: presence)!);
                let ret: NSDictionary = [
                    "presence": presence,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func isReady(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            let ret: NSDictionary = [
                "isReady": carrierHandler.mCarrier.isReady(),
                ]
            self.success(command, retAsDict: ret);
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func getFriends(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let friends: [FriendInfo] = try carrierHandler.mCarrier.getFriends();
                let ret: NSDictionary = [
                    "friends": carrierHandler.getFriendsInfoDict(friends),
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func getFriend(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let userId = command.arguments[1] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let info: FriendInfo = try carrierHandler.mCarrier.getFriendInfo(userId);
                let ret: NSDictionary = carrierHandler.getFriendInfoDict(info);
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func labelFriend(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let userId = command.arguments[1] as? String ?? ""
        let label = command.arguments[2] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.setFriendLabel(forFriend: userId, newLabel: label);
                let ret: NSDictionary = [
                    "userId": userId,
                    "label": label,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func isFriend(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let userId = command.arguments[1] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            let ret: NSDictionary = [
                "userId": userId,
                "isFriend": carrierHandler.mCarrier.isFriend(with: userId),
                ]
            self.success(command, retAsDict: ret);
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func acceptFriend(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let userId = command.arguments[1] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.acceptFriend(with: userId);
                let ret: NSDictionary = [
                    "userId": userId,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func addFriend(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let address = command.arguments[1] as? String ?? ""
        let hello = command.arguments[2] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.addFriend(with: address, withGreeting: hello);
                let ret: NSDictionary = [
                    "address": address,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func removeFriend(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let userId = command.arguments[1] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.removeFriend(userId);
                let ret: NSDictionary = [
                    "userId": userId,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func sendFriendMessage(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let to = command.arguments[1] as? String ?? ""
        let message = command.arguments[2] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.sendFriendMessage(to: to, withMessage: message);
                self.success(command, retAsString: "success!");
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func inviteFriend(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let to = command.arguments[1] as? String ?? ""
        let data = command.arguments[2] as? String ?? ""
        let handlerId = command.arguments[3] as? Int ?? 0
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let handler = FIRHandler(handlerId, FIRCallbackId, self.commandDelegate);
                try carrierHandler.mCarrier.sendInviteFriendRequest(to: to, withData: data, responseHandler: handler.onReceived(_:_:_:_:_:));
                let ret: NSDictionary = [
                    "to": to,
                    "data": data,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func replyFriendInvite(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let to = command.arguments[1] as? String ?? ""
        let status = command.arguments[2] as? Int ?? 0
        let reason = command.arguments[3] as? String ?? ""
        let data = command.arguments[4] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                try carrierHandler.mCarrier.replyFriendInviteRequest(to: to, withStatus: status, reason: reason, data: data);
                let ret: NSDictionary = [
                    "to": to,
                    "status": status,
                    "reason": reason,
                    "data": data,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func destroy(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            carrierHandler.mCarrier.kill();
            let ret: NSDictionary = [:];
            self.success(command, retAsDict: ret);
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func newSession(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let toId = command.arguments[1] as? String ?? ""
        if let carrierHandler: PluginCarrierHandler = mCarrierDict[id] {
            do {
                let session: Session = try carrierHandler.mSessionManager.createSession(to: toId);

                count += 1;
                mSessionDict[count] = session;
                let ret: NSDictionary = [
                    "id": count,
                    "peer": session.getPeer(),
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func sessionClose(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let session: Session = mSessionDict[id] {
            session.close();
            self.success(command, retAsString: "success!");
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func getPeer(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        if let session: Session = mSessionDict[id] {
            let peer = session.getPeer();
            let ret: NSDictionary = [
                "peer": peer,
                ]
            self.success(command, retAsDict: ret);
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func sessionRequest(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let handlerId = command.arguments[1] as? Int ?? 0
        if let session: Session = mSessionDict[id] {
            do {
                let handler = SRCHandler(handlerId, sessionCallbackId, self.commandDelegate);
                try session.sendInviteRequest(handler: handler.onCompletion(_:_:_:_:));
                self.success(command, retAsString: "success!");
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func sessionReplyRequest(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let status = command.arguments[1] as? Int ?? 0
        let reason = command.arguments[2] as? String ?? nil

        if let session: Session = mSessionDict[id] {
            do {
                try session.replyInviteRequest(with: status, reason: reason);
                let ret: NSDictionary = [
                    "status": status,
                    "reason": reason,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func sessionStart(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let sdp = command.arguments[1] as? String ?? ""
        //
        if let session: Session = mSessionDict[id] {
            do {
                try session.start(remoteSdp: sdp);
                let ret: NSDictionary = [
                    "sdp": sdp,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func addStream(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let type = command.arguments[1] as? Int ?? 0
        let options = command.arguments[2] as? Int ?? 0

        if let session: Session = mSessionDict[id] {
            do {
                let streamHandler = try PluginStreamHandler.createInstance(session, type, options, streamCallbackId, self.commandDelegate);

                count += 1;
                streamHandler.mCode = count;
                mStreamDict[count] = streamHandler;
                let ret: NSDictionary = [
                    "objId": streamHandler.mCode,
                    "id": count, //streamHandler.mStream.getStreamId(),
                    "type": type,
                    "options": options,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func removeStream(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let streamId = command.arguments[1] as? Int ?? 0

        if let session: Session = mSessionDict[id], let streamHandler: PluginStreamHandler = mStreamDict[streamId] {
            do {
                try session.removeStream(stream: streamHandler.mStream);
                self.success(command, retAsString: "success!");
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func addService(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let service = command.arguments[1] as? String ?? ""
        let _protocol = command.arguments[2] as? Int ?? 0
        let host = command.arguments[3] as? String ?? ""
        let port = command.arguments[4] as? String ?? ""

        if let session: Session = mSessionDict[id] {
            do {
                try session.addService(serviceName: service, protocol: PortForwardingProtocol(rawValue: _protocol)!, host: host, port: port);
                let ret: NSDictionary = [
                    "service": service,
                    "protocol": _protocol,
                    "host": host,
                    "port": port,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func removeService(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let service = command.arguments[1] as? String ?? ""

        if let session: Session = mSessionDict[id] {
            session.removeService(serviceName: service);
            let ret: NSDictionary = [
                "service": service,
                ]
            self.success(command, retAsDict: ret);
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func getTransportInfo(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0

        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                let ret: NSDictionary = try streamHandler.getTransportInfoDict();
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func streamWrite(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let data = command.arguments[1] as? String ?? ""
        let rawData = data.data(using: String.Encoding.utf8)

        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                let written  = try streamHandler.mStream.writeData(rawData!);
                let ret: NSDictionary = [
                    "written": written,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func openChannel(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let cookie = command.arguments[1] as? String ?? ""


        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                let channel  = try streamHandler.mStream.openChannel(cookie: cookie);
                let ret: NSDictionary = [
                    "channel": channel,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func closeChannel(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let channel = command.arguments[1] as? Int ?? 0

        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                try streamHandler.mStream.closeChannel(channel);
                let ret: NSDictionary = [
                    "channel": channel,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func writeChannel(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let channel = command.arguments[1] as? Int ?? 0
        let data = command.arguments[2] as? String ?? ""
        let rawData = data.data(using: String.Encoding.utf8)

        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                let written  = try streamHandler.mStream.writeChannel(channel, data: rawData!);
                let ret: NSDictionary = [
                    "channel": channel,
                    "written": written,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func pendChannel(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let channel = command.arguments[1] as? Int ?? 0


        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                try streamHandler.mStream.pendChannel(channel);
                let ret: NSDictionary = [
                    "channel": channel,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func resumeChannel(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let channel = command.arguments[1] as? Int ?? 0


        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                try streamHandler.mStream.resumeChannel(channel);
                let ret: NSDictionary = [
                    "channel": channel,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func openPortForwarding(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let service = command.arguments[1] as? String ?? ""
        let _protocol = command.arguments[2] as? Int ?? 0
        let host = command.arguments[3] as? String ?? ""
        let port = command.arguments[4] as? String ?? ""

        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                let pfId = try streamHandler.mStream.openPortForwarding(service: service, ptotocol: PortForwardingProtocol(rawValue: _protocol)!, host: host, port: port);
                let ret: NSDictionary = [
                    "pfId": pfId,
                    "service": service,
                    "protocol": _protocol,
                    "host": host,
                    "port": port,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }

    @objc func closePortForwarding(_ command: CDVInvokedUrlCommand) {
        let id = command.arguments[0] as? Int ?? 0
        let pfId = command.arguments[1] as? Int ?? 0

        if let streamHandler: PluginStreamHandler = mStreamDict[id] {
            do {
                try streamHandler.mStream.closePortForwarding(pfId);
                let ret: NSDictionary = [
                    "pfId": pfId,
                    ]
                self.success(command, retAsDict: ret);
            }
            catch {
                self.error(command, retAsString: error.localizedDescription);
            }
        }
        else {
            self.error(command, retAsString: "Id invalid!");
        }
    }
}
