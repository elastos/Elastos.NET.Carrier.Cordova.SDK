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

//@objc(PluginFileTransferHandler)
class PluginFileTransferHandler: CarrierFileTransferDelegate {
    var fileTransfer:CarrierFileTransfer?
    var fileTransferId:Int = 0
    var callbackId:String?
    var commandDelegate:CDVCommandDelegate?

    init(_ callbackId: String, _ commandDelegate:CDVCommandDelegate) {
        self.callbackId = callbackId;
        self.commandDelegate = commandDelegate;
    }
    
    func setFileTransfer(_ fileTransfer: CarrierFileTransfer){
        self.fileTransfer = fileTransfer;
    }
    
    func setFileTransferId(_ fileTransferId: Int){
        self.fileTransferId = fileTransferId;
    }

    private func sendEvent(_ ret: NSMutableDictionary) {
        ret["fileTransferId"] = fileTransferId
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: ret as? [AnyHashable : Any]);
        result?.setKeepCallbackAs(true);
        self.commandDelegate?.send(result, callbackId:self.callbackId);
    }
    
    func fileTransferStateDidChange(_ fileTransfer: CarrierFileTransfer,
                                    _ newState: CarrierFileTransferConnectionState){
        let ret: NSMutableDictionary = [
            "name": "onStateChanged",
            "state": newState.description,
        ]
        sendEvent(ret);
    }

    
    func didReceiveFileRequest(_ fileTransfer: CarrierFileTransfer,
                               _ fileId: String,
                               _ fileName: String,
                               _ fileSize: UInt64){
        let ret: NSMutableDictionary = [
            "name": "onFileRequest",
            "fileId": fileId,
            "filename": fileName,
            "size": fileSize,
        ]
        sendEvent(ret);
    }

    func didReceivePullRequest(_ fileTransfer: CarrierFileTransfer,
                               _ fileId: String,
                               _ offset: UInt64){
        let ret: NSMutableDictionary = [
            "name": "onPullRequest",
            "fileId": fileId,
            "offset": offset,
        ]
        sendEvent(ret);
    }

    func didReceiveFileTransferData(_ fileTransfer: CarrierFileTransfer,
                                    _ fileId: String,
                                    _ data: Data) -> Bool{
        let ret: NSMutableDictionary = [
            "name": "onData",
            "fileId": fileId,
            "data": String(data: data, encoding: .utf8)!,
        ]
        sendEvent(ret);
        return true
    }


    func fileTransferPending(_ fileTransfer: CarrierFileTransfer,
                             _ fileId: String){
        let ret: NSMutableDictionary = [
                   "name": "onPending",
                   "fileId": fileId,
        ]
        sendEvent(ret);
    }

    func fileTransferResumed(_ fileTransfer: CarrierFileTransfer,
                             _ fileId: String){
        let ret: NSMutableDictionary = [
                   "name": "onResume",
                   "fileId": fileId,
        ]
        sendEvent(ret);
    }

    func fileTransferWillCancel(_ fileTransfer: CarrierFileTransfer,
                                _ fileId: String,
                                _ status: Int,
                                _ reason: String){
        let ret: NSMutableDictionary = [
                   "name": "onCancel",
                   "fileId": fileId,
                   "status": status,
                   "reason": reason,
                   
        ]
        sendEvent(ret);
    }
}
