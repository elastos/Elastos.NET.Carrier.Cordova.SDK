import Foundation
import ElastosCarrier

typealias Stream = ElastosCarrier.CarrierStream
typealias AddressInfo = ElastosCarrier.CarrierAddressInfo
typealias TransportInfo = ElastosCarrier.CarrierTransportInfo
//typealias Session = ElastosCarrier.CarrierSession

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

    static func createInstance(_ session: Session, _ type: Int, _ options: Int, _ callbackId:String, _ commandDelegate:CDVCommandDelegate) -> PluginStreamHandler {
        let handler = PluginStreamHandler(callbackId, commandDelegate);
        do {
            handler.mStream = try session.addStream(type: CarrierStreamType(rawValue: type)!, options: CarrierStreamOptions(rawValue: CarrierStreamOptions.RawValue(options)), delegate: handler);
        }
        catch {
        }
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

    func getTransportInfoDict()  -> NSMutableDictionary {
        var ret: NSMutableDictionary?
        do {
            let info:TransportInfo = try mStream.getTransportInfo();
            ret = [
                "topology": info.networkTopology,
                "local": getAddressInfoDict(info: info.localAddressInfo),
                "remote": getAddressInfoDict(info: info.remoteAddressInfo),
                ]
        }
        catch {
        }
        return ret!;
    }

    private func sendEvent(_ ret: NSMutableDictionary) {
        ret["id"] = mCode
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: ret as! [AnyHashable : Any]);
        result?.setKeepCallbackAs(true);
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
            "data": NSString(data: data, encoding: String.Encoding.utf8.rawValue) ?? "",
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
            "data": NSString(data: data, encoding: String.Encoding.utf8.rawValue) ?? "",
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
