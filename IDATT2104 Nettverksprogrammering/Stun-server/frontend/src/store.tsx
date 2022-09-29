import { ReactNode, createContext, useContext, useState } from 'react';
export type StoreType = {
  peerConnection: {
    get: RTCPeerConnection | null;
    set: React.Dispatch<React.SetStateAction<RTCPeerConnection | null>>;
  };
  localStream: {
    get: MediaStream | null;
    set: React.Dispatch<React.SetStateAction<MediaStream | null>>;
  };
  remoteStream: {
    get: MediaStream | null;
    set: React.Dispatch<React.SetStateAction<MediaStream | null>>;
  };
  roomId: {
    get: string | null;
    set: React.Dispatch<React.SetStateAction<string | null>>;
  };
  name: {
    get: string | null;
    set: React.Dispatch<React.SetStateAction<string | null>>;
  };
  remoteName: {
    get: string | null;
    set: React.Dispatch<React.SetStateAction<string | null>>;
  };
  shareVideo: {
    get: boolean;
    set: React.Dispatch<React.SetStateAction<boolean>>;
  };
  shareAudio: {
    get: boolean;
    set: React.Dispatch<React.SetStateAction<boolean>>;
  };
  sharingDisplay: {
    get: boolean;
    set: React.Dispatch<React.SetStateAction<boolean>>;
  };
  callStart: {
    get: Date | null;
    set: React.Dispatch<React.SetStateAction<Date | null>>;
  };
};
const Store = createContext<StoreType | undefined>(undefined);
export type StoreProviderProps = {
  children: ReactNode;
};
export const StoreProvider = ({ children }: StoreProviderProps) => {
  const [peerConnection, setPeerConnection] = useState<RTCPeerConnection | null>(null);
  const [localStream, setLocalStream] = useState<MediaStream | null>(null);
  const [remoteStream, setRemoteStream] = useState<MediaStream | null>(null);
  const [roomId, setRoomId] = useState<string | null>(null);
  const [name, setName] = useState<string | null>(null);
  const [remoteName, setRemoteName] = useState<string | null>(null);
  const [shareVideo, setShareVideo] = useState(true);
  const [shareAudio, setShareAudio] = useState(true);
  const [sharingDisplay, setSharingDisplay] = useState(false);
  const [callStart, setCallStart] = useState<Date | null>(null);
  const store = {
    peerConnection: { get: peerConnection, set: setPeerConnection },
    localStream: { get: localStream, set: setLocalStream },
    remoteStream: { get: remoteStream, set: setRemoteStream },
    roomId: { get: roomId, set: setRoomId },
    name: { get: name, set: setName },
    remoteName: { get: remoteName, set: setRemoteName },
    shareVideo: { get: shareVideo, set: setShareVideo },
    shareAudio: { get: shareAudio, set: setShareAudio },
    sharingDisplay: { get: sharingDisplay, set: setSharingDisplay },
    callStart: { get: callStart, set: setCallStart },
  };
  return <Store.Provider value={store}>{children}</Store.Provider>;
};

export const useStore = () => {
  const context = useContext(Store);
  if (context === undefined) {
    throw new Error('useStore must be used within a StoreProvider');
  }
  return context;
};
