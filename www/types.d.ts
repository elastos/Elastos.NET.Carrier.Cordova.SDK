export declare type onSessionRequestComplete = (session: Session, status: Number, reason: string, sdp: string)=>void;
export declare type onFriendInviteResponse = (from: string, status: Number, reason: string, data: string)=>void;

export declare class BootstrapNode {
    ipv4: string;
    ipv6: string;
    port: string;
    publicKey: string;
}

export declare class Options {
    udpEnabled: Boolean;
    persistentLocation: string;
    bootstraps: BootstrapNode[];
}

export declare class UserInfo {
    userId: string;
    name: string;
    description: string;
    hasAvatar: Boolean;
    gender: string;
    phone: string;
    email: string;
    region: string;
}

export declare class FriendInfo {
    userInfo: UserInfo;
    presence: PresenceStatus;
    connection: ConnectionStatus;
    label: string;
}

export declare class AddressInfo {
    type: CandidateType;
    address: string;
    port: string;
    relatedAddress?: string;
    relatedPort?: string;
}

export declare class TransportInfo {
    topology: NetworkTopology;
    localAddr: AddressInfo;
    remoteAddr: AddressInfo;
}

export declare type StreamCallbacks = {
    onStateChanged: (stream: Stream, state: StreamState)=>void = null;
    onStreamData: (stream: Stream, data: string)=>void = null;
    onChannelOpen: (stream: Stream, channel: Number, cookie: string)=>void = null;
    onChannelOpened: (stream: Stream, channel: Number)=>void;
    onChannelClose: (stream: Stream, channel: Number, reason: string)=>void;
    onChannelData: (stream: Stream, channel: Number, data: string)=>void;
    onChannelPending: (stream: Stream, channel: Number)=>void;
    onChannelResume: (stream: Stream, channel: Number)=>void;
}

export declare class Stream {
    id: Number = null;
    carrier: Carrier = null;
    session: Session = null;

    callbacks: StreamCallbacks;

    getTransportInfo: (onSuccess: (transportInfo: TransportInfo)=>void, onError?:(err: string)=>void)=>void;
    write: (onSuccess:(bytesSent: Number)=>void, onError?:(err: string)=>void, data: string)=>void;
    openChannel: (onSuccess:(channelId: Number)=>void, onError?:(err: string)=>void, cookie: string)=>void;
    closeChannel: (onSuccess:()=>void, onError?:(err: string)=>void, channel: Number)=>void;
    writeChannel: (onSuccess:(bytesSent: Number)=>void, onError?:(err: string)=>void, channel: Number, data: string)=>void;
    pendChannel: (onSuccess:()=>void, onError?:(err: string)=>void, channel: Number)=>void;
    resumeChannel: (onSuccess:()=>void, onError?:(err: string)=>void, channel: Number)=>void;
    openPortForwarding: (onSuccess:(portForwardingId: Number)=>void, onError?:(err: string)=>void, service: string, protocol: PortForwardingProtocol,  host: string, port: Number)=>void;
    closePortForwarding: (onSuccess:()=>void, onError?:(err: string)=>void, portForwarding: Number)=>void;
}

export declare class Session {
    peer: string = null;
    carrier: Carrier = null;

    close: (onSuccess?:()=>void, onError?:(err: string)=>void)=>void;
    request: (onSuccess:()=>void, onError?:(err: string)=>void, handler: onSessionRequestComplete)=>void;
    replyRequest: (onSuccess:()=>void, onError?:(err: string)=>void, status: Number, reason: string)=>void;
    start: (onSuccess:()=>void, onError?:(err: string)=>void, sdp: string)=>void;
    addStream: (onSuccess:()=>void, onError?:(err: string)=>void, type: StreamType, options: Number, callbacks: StreamCallbacks)=>void;
    removeStream: (onSuccess:()=>void, onError?:(err: string)=>void, stream: Stream)=>void;
    addService: (onSuccess:()=>void, onError?:(err: string)=>void, service: string, protocol: PortForwardingProtocol, host: string, port: Number)=>void;
    removeService: (onSuccess:()=>void, onError?:(err: string)=>void, service: string)=>void;
}

export declare type CarrierCallbacks = {
    onConnection: (carrier: Carrier, status: ConnectionStatus)=>void = null;
    onReady: (carrier: Carrier)=>void = null;
    onSelfInfoChanged: (carrier: Carrier, userInfo: UserInfo)=>void = null;
    onFriends: (carrier: Carrier, friends: FriendInfo[])=>void = null;
    onFriendConnection: (carrier: Carrier, friendId: string, status: ConnectionStatus)=>void = null;
    onFriendInfoChanged: (carrier: Carrier, friendId: string, info: FriendInfo)=>void = null;
    onFriendPresence: (carrier: Carrier, friendId: string, presence: PresenceStatus)=>void = null;
    onFriendRequest: (carrier: Carrier, userId: string, info: UserInfo, hello: string)=>void = null;
    onFriendAdded: (carrier: Carrier, friendInfo: FriendInfo)=>void = null;
    onFriendRemoved: (carrier: Carrier, friendId: string)=>void = null;
    onFriendMessage: (carrier: Carrier, from: string, messate: string, isOffline: Boolean)=>void = null;
    onFriendInviteRequest: (carrier: Carrier, from: string, data: string)=>void = null;
    onSessionRequest: (carrier: Carrier, from: string, sdp: string)=>void = null;
    onSessionRequest: (carrier: Carrier, groupTitle: string)=>void = null;
}

export declare class Carrier {
    nodeId: string = null;
    userId: string = null;
    address: string = null;

    callbacks: CarrierCallbacks;

    set nospam(value: Number); // CHECK THIS
    get nospam():Number; // CHECK THIS

    set presence(value: PresenceStatus); // CHECK THIS
    get presence():PresenceStatus; // CHECK THIS

    start: (onSuccess:()=>void, onError?:(err: string)=>void, iterateInterval: Number)=>void;
    getSelfInfo: (onSuccess:(userInfo: UserInfo)=>void, onError?:(err: string)=>void)=>void;
    setSelfInfo: (onSuccess:()=>void, onError?:(err: string)=>void, name: string, value: string)=>void;
    isReady: (onSuccess:(ready: Boolean)=>void, onError?:(err: string)=>void)=>void;
    getFriends: (onSuccess:(friends: FriendInfo[])=>void, onError?:(err: string)=>void)=>void;
    getFriend: (onSuccess:(friend: FriendInfo)=>void, onError?:(err: string)=>void, userId: string)=>void;
    labelFriend: (onSuccess:()=>void, onError?:(err: string)=>void, userId: string, label: string)=>void;
    isFriend: (onSuccess:(isFriend: Boolean)=>void, onError?:(err: string)=>void, userId: string)=>void;
    addFriend: (onSuccess:()=>void, onError?:(err: string)=>void, address: string, hello: string)=>void;
    acceptFriend: (onSuccess:()=>void, onError?:(err: string)=>void, userId: string)=>void;
    removeFriend: (onSuccess:()=>void, onError?:(err: string)=>void, userId: string)=>void;
    sendFriendMessage: (onSuccess:()=>void, onError?:(err: string)=>void, to: string, message: string)=>void;
    inviteFriend: (onSuccess:()=>void, onError?:(err: string)=>void, to: string, data: string, handler: onFriendInviteResponse)=>void;
    replyFriendInvite: (onSuccess:()=>void, onError?:(err: string)=>void, to: string, status: Number, reason: string, data: string)=>void;
    newGroup: (onSuccess:(group: Group)=>void, onError?:(err: string)=>void, callbacks: CarrierCallbacks)=>void;
    groupJoin: (onSuccess:(group: Group)=>void, onError?:(err: string)=>void, friendId: string, cookieCode: string)=>void;
    groupLeave: (onSuccess:()=>void, onError?:(err: string)=>void, group: Group)=>void;
    getGroups: (onSuccess:(groups: Group[])=>void, onError?:(err: string)=>void)=>void;
    newSession: (onSuccess:(session: Session)=>void, onError?:(err: string)=>void, to: string)=>void;
    destroy: (onSuccess:()=>void, onError?:(err: string)=>void)=>void;
}

export declare type GroupCallbacks = {
    onGroupConnected: ()=>void;
    onGroupMessage: (from: string, message: string)=>void;
    onGroupTitle: (from: string, title: string)=>void;
    onPeerName: (peerId: string, peerName: string)=>void;
    onPeerListChanged: ()=>void;
}

export declare class Group {
    groupId: Number;
    callbacks: GroupCallbacks;

    invite: (onSuccess:()=>void, onError?:(err: string)=>void, friendId: string)=>void;
    sendMessage: (onSuccess:()=>void, onError?:(err: string)=>void, message: string)=>void;
    getTitle: (onSuccess:(groupTitle: string)=>void, onError?:(err: string)=>void)=>void;
    setTitle: (onSuccess:(groupTitle: string)=>void, onError?:(err: string)=>void, groupTitle: string)=>void;
    getPeers: (onSuccess:(peers: any)=>void, onError?:(err: string)=>void)=>void; // TODO: define a Peer type
    getPeers: (onSuccess:(peer: any)=>void, onError?:(err: string)=>void, peerId: string)=>void; // TODO: define a Peer type
}

export declare enum ConnectionStatus {
    CONNECTED=0,
    DISCONNECTED=1
}

export declare enum PresenceStatus {
    NONE=0,
    AWAY=1,
    BUSY=2
}

export declare enum StreamType {
    AUDIO=0,
    VIDEO=1,
    TEXT=2,
    APPLICATION=3,
    MESSAGE=4
}

export declare enum StreamState {
    RAW=0,
    INITIALIZED=1,
    TRANSPORT_READY=2,
    CONNECTING=3,
    CONNECTED=4,
    DEACTIVATED=5,
    CLOSED=6,
    ERROR=7
}

export declare enum CandidateType {
    HOST=0,
    SERVER_REFLEXIVE=1,
    PEER_REFLEXIVE=2,
    RELAYED=3
}
    
export declare enum NetworkTopology {
    LAN=0,
    P2P=1,
    RELAYED=2
}
    
export declare enum PortForwardingProtocol {
    TCP=1
}

export declare enum CloseReason {
    NORMAL=0,
    TIMEOUT=1,
    ERROR=2
}
   
export declare enum StreamMode {
    COMPRESS=1,
    PLAIN=2,
    RELIABLE=4,
    MULTIPLEXING=8,
    PORT_FORWARDING=16
}

export declare class CarrierPlugin {
    getVersion: (onSuccess:(version: string)=>void, onError?:(err: string)=>void)=>void;
    isValidId: (onSuccess:(isValid: Boolean)=>void, onError?:(err: string)=>void, id: string)=>void;
    isValidAddress: (onSuccess:(isValid: string)=>void, onError?:(err: string)=>void, address: string)=>void;
    getIdFromAddress: (onSuccess:(userId: string)=>void, onError?:(err: string)=>void, address: string)=>void;
    createObject: (onSuccess:(carrier: Carrier)=>void, onError?:(err: string)=>void, options: any, callbacks: CarrierCallbacks)=>void; // TODO: need a type for options
}
