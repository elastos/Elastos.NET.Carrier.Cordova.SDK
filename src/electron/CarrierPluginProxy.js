var CarrierPluginProxy = {
    test: async function(success, error, opts) {
        await window.carrierManagerImpl.test(success, error, opts);
    },
    
    carrierStart: async function(success, error, opts) {
		await window.carrierManagerImpl.carrierStart(success, error, opts);
    },
	
	createObject: async function(success, error, opts) {
        await window.carrierManagerImpl.createObject(success, error, opts);
    },
	
	setListener: function(success, error, opts) {
		window.carrierManagerImpl.setListener((args) => {
            success(args, {keepCallback: true});
        }, error, opts);
    },

    isReady: async function(success, error, opts) {
        await window.carrierManagerImpl.isReady(success, error, opts);
    },

    acceptFriend: async function(success, error, opts) {
        await window.carrierManagerImpl.acceptFriend(success, error, opts);
    },

    addFriend: async function(success, error, opts) {
        await window.carrierManagerImpl.addFriend(success, error, opts);
    },

    getFriend: async function(success, error, opts) {
        await window.carrierManagerImpl.getFriend(success, error, opts);
    },

    labelFriend: async function(success, error, opts) {
        await window.carrierManagerImpl.labelFriend(success, error, opts);
    },

    isFriend: async function(success, error, opts) {
        await window.carrierManagerImpl.isFriend(success, error, opts);
    },

    removeFriend: async function(success, error, opts) {
        await window.carrierManagerImpl.removeFriend(success, error, opts);
    },

    getFriends: async function(success, error, opts) {
        await window.carrierManagerImpl.getFriends(success, error, opts);
    },

    sendFriendMessage: async function(success, error, opts) {
        await window.carrierManagerImpl.sendFriendMessage(success, error, opts);
    },

    sendFriendBinaryMessage: async function(success, error, opts) {
        await window.carrierManagerImpl.sendFriendBinaryMessage(success, error, opts);
    },

    sendFriendMessageWithReceipt: async function(success, error, opts) {
        await window.carrierManagerImpl.sendFriendMessageWithReceipt(success, error, opts);
    },

    sendFriendBinaryMessageWithReceipt: async function(success, error, opts) {
        await window.carrierManagerImpl.sendFriendBinaryMessageWithReceipt(success, error, opts);
    },

    getSelfInfo: async function(success, error, opts) {
        await window.carrierManagerImpl.getSelfInfo(success, error, opts);
    },

    setSelfInfo: async function(success, error, opts) {
        await window.carrierManagerImpl.setSelfInfo(success, error, opts);
    },

    getNospam: async function(success, error, opts) {
        await window.carrierManagerImpl.getNospam(success, error, opts);
    },

    setNospam: async function(success, error, opts) {
        await window.carrierManagerImpl.setNospam(success, error, opts);
    },

    getPresence: async function(success, error, opts) {
        await window.carrierManagerImpl.getPresence(success, error, opts);
    },

    setPresence: async function(success, error, opts) {
        await window.carrierManagerImpl.setPresence(success, error, opts);
    },

    inviteFriend: async function(success, error, opts) {
        await window.carrierManagerImpl.inviteFriend(success, error, opts);
    },

    newSession: async function(success, error, opts) {
        await window.carrierManagerImpl.newSession(success, error, opts);
    },

    destroy: async function(success, error, opts) {
        await window.carrierManagerImpl.destroy(success, error, opts);
    },

    sessionClose: async function(success, error, opts) {
        await window.carrierManagerImpl.sessionClose(success, error, opts);
    },

    getPeer: async function(success, error, opts) {
        await window.carrierManagerImpl.getPeer(success, error, opts);
    },

    sessionRequest: async function(success, error, opts) {
        await window.carrierManagerImpl.sessionRequest(success, error, opts);
    },

    sessionReplyRequest: async function(success, error, opts) {
        await window.carrierManagerImpl.sessionReplyRequest(success, error, opts);
    },

    sessionStart: async function(success, error, opts) {
        await window.carrierManagerImpl.sessionStart(success, error, opts);
    },

    addStream: async function(success, error, opts) {
        await window.carrierManagerImpl.addStream(success, error, opts);
    },

    removeStream: async function(success, error, opts) {
        await window.carrierManagerImpl.removeStream(success, error, opts);
    },

    addService: async function(success, error, opts) {
        await window.carrierManagerImpl.addService(success, error, opts);
    },

    removeService: async function(success, error, opts) {
        await window.carrierManagerImpl.removeService(success, error, opts);
    },

    getTransportInfo: async function(success, error, opts) {
        await window.carrierManagerImpl.getTransportInfo(success, error, opts);
    },

    streamWrite: async function(success, error, opts) {
        await window.carrierManagerImpl.streamWrite(success, error, opts);
    },

    openChannel: async function(success, error, opts) {
        await window.carrierManagerImpl.openChannel(success, error, opts);
    },

    closeChannel: async function(success, error, opts) {
        await window.carrierManagerImpl.closeChannel(success, error, opts);
    },

    writeChannel: async function(success, error, opts) {
        await window.carrierManagerImpl.writeChannel(success, error, opts);
    },

    pendChannel: async function(success, error, opts) {
        await window.carrierManagerImpl.pendChannel(success, error, opts);
    },

    resumeChannel: async function(success, error, opts) {
        await window.carrierManagerImpl.resumeChannel(success, error, opts);
    },

    openPortForwarding: async function(success, error, opts) {
        await window.carrierManagerImpl.openPortForwarding(success, error, opts);
    },

    closePortForwarding: async function(success, error, opts) {
        await window.carrierManagerImpl.closePortForwarding(success, error, opts);
    },
    //static
    getVersion: async function(success, error, opts) {
        await window.carrierManagerImpl.getVersion(success, error, opts);
    },

    getIdFromAddress: async function(success, error, opts) {
        await window.carrierManagerImpl.getIdFromAddress(success, error, opts);
    },

    isValidAddress: async function(success, error, opts) {
        await window.carrierManagerImpl.isValidAddress(success, error, opts);
    },

    isValidId: async function(success, error, opts) {
        await window.carrierManagerImpl.isValidId(success, error, opts);
    },

    createGroup: async function(success, error, opts) {
        await window.carrierManagerImpl.createGroup(success, error, opts);
    },

    joinGroup: async function(success, error, opts) {
        await window.carrierManagerImpl.joinGroup(success, error, opts);
    },

    inviteGroup: async function(success, error, opts) {
        await window.carrierManagerImpl.inviteGroup(success, error, opts);
    },

    leaveGroup: async function(success, error, opts) {
        await window.carrierManagerImpl.leaveGroup(success, error, opts);
    },

    sendGroupMessage: async function(success, error, opts) {
        await window.carrierManagerImpl.sendGroupMessage(success, error, opts);
    },

    getGroupTitle: async function(success, error, opts) {
        await window.carrierManagerImpl.getGroupTitle(success, error, opts);
    },

    setGroupTitle: async function(success, error, opts) {
        await window.carrierManagerImpl.setGroupTitle(success, error, opts);
    },

    getGroupPeers: async function(success, error, opts) {
        await window.carrierManagerImpl.getGroupPeers(success, error, opts);
    },

    getGroupPeer: async function(success, error, opts) {
        await window.carrierManagerImpl.getGroupPeer(success, error, opts);
    },

    generateFileTransFileId: async function(success, error, opts) {
        await window.carrierManagerImpl.generateFileTransFileId(success, error, opts);
    },

    closeFileTrans: async function(success, error, opts) {
        await window.carrierManagerImpl.closeFileTrans(success, error, opts);
    },

    getFileTransFileId: async function(success, error, opts) {
        await window.carrierManagerImpl.getFileTransFileId(success, error, opts);
    },

    getFileTransFileName: async function(success, error, opts) {
        await window.carrierManagerImpl.getFileTransFileName(success, error, opts);
    },

    fileTransConnect: async function(success, error, opts) {
        await window.carrierManagerImpl.fileTransConnect(success, error, opts);
    },

    acceptFileTransConnect: async function(success, error, opts) {
        await window.carrierManagerImpl.acceptFileTransConnect(success, error, opts);
    },

    addFileTransFile: async function(success, error, opts) {
        await window.carrierManagerImpl.addFileTransFile(success, error, opts);
    },

    pullFileTransData: async function(success, error, opts) {
        await window.carrierManagerImpl.pullFileTransData(success, error, opts);
    },

    writeFileTransData: async function(success, error, opts) {
        await window.carrierManagerImpl.writeFileTransData(success, error, opts);
    },

    sendFileTransFinish: async function(success, error, opts) {
        await window.carrierManagerImpl.sendFileTransFinish(success, error, opts);
    },

    cancelFileTrans: async function(success, error, opts) {
        await window.carrierManagerImpl.cancelFileTrans(success, error, opts);
    },

    pendFileTrans: async function(success, error, opts) {
        await window.carrierManagerImpl.pendFileTrans(success, error, opts);
    },

    resumeFileTrans: async function(success, error, opts) {
        await window.carrierManagerImpl.resumeFileTrans(success, error, opts);
    },

    newFileTransfer: async function(success, error, opts) {
        await window.carrierManagerImpl.newFileTransfer(success, error, opts);
    }
};

module.exports = CarrierPluginProxy;

require("cordova/exec/proxy").add("Carrier", CarrierPluginProxy);

