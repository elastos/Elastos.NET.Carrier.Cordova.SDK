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

typealias Stream = ElastosCarrierSDK.CarrierStream
typealias AddressInfo = ElastosCarrierSDK.CarrierAddressInfo
typealias TransportInfo = ElastosCarrierSDK.CarrierTransportInfo
//typealias Session = ElastosCarrierSDK.CarrierSession

class PluginStreamHandler: CarrierStreamDelegate {
    //    let String TAG:String = "PluginStreamHandler";

    var mStream: Stream!;
    var mCode:Int = 0
    var callbackId:String?
    var commandDelegate:CDVCommandDelegate?

    init(_ callbackId: String, _ commandDelegate:CDVCommandDelegate) {
        self.callbackId = callbackId;
        self.commandDelegate = commandDelegate;
    }

    static func createInstance(_ session: Session, _ type: Int, _ options: Int, _ callbackId:String, _ commandDelegate:CDVCommandDelegate) throws -> PluginStreamHandler {
        let handler = PluginStreamHandler(callbackId, commandDelegate);

        handler.mStream = try session.addStream(type: CarrierStreamType(rawValue: type)!, options: CarrierStreamOptions(rawValue: CarrierStreamOptions.RawValue(options)), delegate: handler);

        return handler;
    }

    func getAddressInfoDict(info: AddressInfo) -> NSMutableDictionary {
        let ret: NSMutableDictionary = [
            "type": info.candidateType.rawValue,
            "address": info.address.hostname,
            "port": info.address.port,
            "relatedAddress": info.relatedAddress?.hostname ?? "",
            "relatedPort": info.relatedAddress?.port ?? 0,
            ]
        return ret;
    }

    func getTransportInfoDict() throws -> NSMutableDictionary {
        var ret: NSMutableDictionary?

        let info:TransportInfo = try mStream.getTransportInfo();
        ret = [
            "topology": info.networkTopology.rawValue,
            "localAddr": getAddressInfoDict(info: info.localAddressInfo),
            "remoteAddr": getAddressInfoDict(info: info.remoteAddressInfo),
            ]

        return ret!;
    }

    private func sendEvent(_ ret: NSMutableDictionary) {
        ret["id"] = mCode
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: ret as? [AnyHashable : Any]);
        result!.setKeepCallbackAs(true);
        self.commandDelegate?.send(result, callbackId:self.callbackId);

    }

    func streamStateDidChange(_ stream: CarrierStream,
                              _ newState: CarrierStreamState) {
        let ret: NSMutableDictionary = [
            "name": "onStateChanged",
            "state": newState.rawValue,
            ]
        sendEvent(ret);
    }

    func didReceiveStreamData(_ stream: CarrierStream,
                              _ data: Data) {
        let ret: NSMutableDictionary = [
            "name": "onStreamData",
            "data": NSString(data: data.base64EncodedData(), encoding: String.Encoding.utf8.rawValue) ?? "",
            ]
        sendEvent(ret);
    }

    func shouldOpenNewChannel(_ stream: CarrierStream,
                              _ wantChannel: Int,
                              _ cookie: String) -> Bool {
        let ret: NSMutableDictionary = [
            "name": "onChannelOpen",
            "channel": wantChannel,
            "cookie": cookie,
            ]
        sendEvent(ret);
        return true;
    }

    func didOpenNewChannel(_ stream: CarrierStream,
                           _ newChannel: Int) {
        let ret: NSMutableDictionary = [
            "name": "onChannelOpened",
            "channel": newChannel,
            ]
        sendEvent(ret);
    }

    func didCloseChannel(_ stream: CarrierStream,
                         _ channel: Int,
                         _ reason: CloseReason) {
        let ret: NSMutableDictionary = [
            "name": "onChannelClose",
            "channel": channel,
            "reason": reason.rawValue,
            ]
        sendEvent(ret);
    }

    func didReceiveChannelData(_ stream: CarrierStream,
                               _ channel: Int,
                               _ data: Data) -> Bool {
        let ret: NSMutableDictionary = [
            "name": "onChannelData",
            "channel": channel,
            "data": NSString(data: data.base64EncodedData(), encoding: String.Encoding.utf8.rawValue) ?? "",
            ]
        sendEvent(ret);
        return true;
    }

    func channelPending(_ stream: CarrierStream,
                        _ channel: Int) {
        let ret: NSMutableDictionary = [
            "name": "onChannelPending",
            "channel": channel,
            ]
        sendEvent(ret);
    }

    func channelResumed(_ stream: CarrierStream,
                        _ channel: Int) {
        let ret: NSMutableDictionary = [
            "name": "onChannelResume",
            "channel": channel,
            ]
        sendEvent(ret);
    }
}
