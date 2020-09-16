let fs = require("fs")
const { contextBridge, ipcRenderer, remote } = require("electron")
require("../../../../trinity-renderer")

contextBridge.exposeInMainWorld(
    'carrierManagerImpl',
    TrinityRenderer.TrinityRuntimeHelper.createIPCDefinitionToMainProcess("CarrierPlugin", [
        "test",
        "setListener",
        "createObject",
		"carrierStart",
        "isReady",
        "acceptFriend",
        "addFriend",
        "getFriend",
        "labelFriend",
        "isFriend",
        "removeFriend",
        "getFriends",
        "sendFriendMessage",
        "sendFriendBinaryMessage",
        "sendFriendMessageWithReceipt",
        "sendFriendBinaryMessageWithReceipt",
        "getSelfInfo",
        "setSelfInfo",
        "getNospam",
        "setNospam",
        "getPresence",
        "setPresence",
        "inviteFriend",
        "replyFriendInvite",
        "destroy",
        "newSession",
        "sessionClose",
        "getPeer",
        "sessionRequest",
        "sessionReplyRequest",
        "sessionStart",
        "addStream",
        "removeStream",
        "addService",
        "removeService",
        "getTransportInfo",
        "streamWrite",
        "openChannel",
        "closeChannel",
        "writeChannel",
        "pendChannel",
        "resumeChannel",
        "openPortForwarding",
        "closePortForwarding",
        "getVersion",
        "getIdFromAddress",
        "isValidAddress",
        "isValidId",
        "createGroup",
        "joinGroup",
        "inviteGroup",
        "leaveGroup",
        "sendGroupMessage",
        "getGroupTitle",
        "setGroupTitle",
        "getGroupPeers",
        "getGroupPeer",
        "generateFileTransFileId",
        "closeFileTrans",
        "getFileTransFileId",
        "getFileTransFileName",
        "fileTransConnect",
        "acceptFileTransConnect",
        "addFileTransFile",
        "pullFileTransData",
        "writeFileTransData",
        "sendFileTransFinish",
        "cancelFileTrans",
        "pendFileTrans",
        "resumeFileTrans",
        "newFileTransfer"
    ])
)