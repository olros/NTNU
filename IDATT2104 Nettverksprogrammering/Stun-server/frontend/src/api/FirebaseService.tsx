/* eslint-disable no-console */
import { db, COLLECTIONS } from 'fb';
import URLS from 'URLS';
import { StoreType } from 'store';
import { SnackbarProps } from 'Snackbar';

const configuration = {
  iceServers: [
    {
      // urls: ['stun:stun1.l.google.com:19302', 'stun:stun2.l.google.com:19302'],
      urls: ['stun:stunserver.northeurope.cloudapp.azure.com:3478'],
    },
  ],
  iceCandidatePoolSize: 10,
};

export const openUserMedia = async (localVideo: React.RefObject<HTMLVideoElement>, store: StoreType) => {
  const stream = await navigator.mediaDevices.getUserMedia({
    video: store.shareVideo.get,
    audio: store.shareAudio.get,
  });
  if (localVideo.current) {
    localVideo.current.srcObject = stream;
  }
  store.localStream.set(stream);

  const newMediaStream = new MediaStream();
  store.remoteStream.set(newMediaStream);
};

export const setScreenUserMedia = async (localVideo: React.RefObject<HTMLVideoElement>, store: StoreType, showSnackbar: SnackbarProps) => {
  try {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const stream: MediaStream = await navigator.mediaDevices.getDisplayMedia();
    store.localStream.get?.getTracks().forEach(function (track) {
      track.stop();
    });
    if (localVideo.current) {
      localVideo.current.srcObject = stream;
    }
    store.localStream.set(stream);

    const newPeerConnection = store.peerConnection.get;
    if (newPeerConnection) {
      newPeerConnection.getSenders().map((sender) => sender.replaceTrack(stream.getTracks().find((track) => track.kind === sender.track?.kind) || null));
      store.peerConnection.set(newPeerConnection);
      store.sharingDisplay.set(true);
    }
  } catch (e) {
    showSnackbar('Denne enheten støtter ikke skjermdeling', 'error');
  }
};

export const setCameraUserMedia = async (
  localVideo: React.RefObject<HTMLVideoElement>,
  store: StoreType,
  video = store.shareVideo.get,
  audio = store.shareAudio.get,
) => {
  store.localStream.get?.getTracks().forEach((track) => track.stop());
  const stream = await navigator.mediaDevices.getUserMedia({
    video: video,
    audio: audio,
  });
  store.shareVideo.set(video);
  store.shareAudio.set(audio);
  if (localVideo.current) {
    localVideo.current.srcObject = stream;
  }
  store.localStream.set(stream);

  const newPeerConnection = store.peerConnection.get;
  if (newPeerConnection) {
    newPeerConnection.getSenders().map((sender) => sender.replaceTrack(stream.getTracks().find((track) => track.kind === sender.track?.kind) || null));
    store.peerConnection.set(newPeerConnection);
    store.sharingDisplay.set(false);
  }
};

export const createRoom = async (store: StoreType) => {
  const roomRef = await db.rooms.doc();

  console.log('Create PeerConnection with configuration: ', configuration);
  const peerConnection = new RTCPeerConnection(configuration);
  const localStream = store.localStream.get;
  if (localStream) {
    localStream.getTracks().forEach((track) => {
      peerConnection.addTrack(track, localStream);
    });
  }

  // Code for collecting ICE candidates below
  const callerCandidatesCollection = roomRef.collection(COLLECTIONS.CALLERCANDIDATES);

  peerConnection.addEventListener('icecandidate', (event) => {
    if (!event.candidate) {
      console.log('Got final candidate!');
      return;
    }
    console.log('Got candidate: ', event.candidate);
    callerCandidatesCollection.add(event.candidate.toJSON());
  });
  // Code for collecting ICE candidates above

  // Code for creating a room below
  const offer = await peerConnection.createOffer();
  await peerConnection.setLocalDescription(offer);
  console.log('Created offer:', offer);

  const name = store.name.get || '-';
  const roomWithOffer = {
    offer: {
      type: offer.type || 'offer',
      sdp: offer.sdp || '',
      name: name,
    },
  };
  await roomRef.set(roomWithOffer);
  store.roomId.set(roomRef.id);
  console.log(`New room created with SDP offer. Room ID: ${roomRef.id}`);
  // Code for creating a room above

  peerConnection.addEventListener('track', (event) => {
    console.log('Got remote track:', event.streams[0]);
    event.streams[0].getTracks().forEach((track) => {
      console.log('Add a track to the remoteStream:', track);
      store.remoteStream.get?.addTrack(track);
    });
  });

  // Listening for remote session description below
  roomRef.onSnapshot(async (snapshot) => {
    const data = snapshot.data();
    if (!peerConnection.currentRemoteDescription && data && data.answer) {
      console.log('Got remote description: ', data.answer);
      store.remoteName.set(data.answer.name);
      store.callStart.set(new Date());
      const rtcSessionDescription = new RTCSessionDescription(data.answer);
      await peerConnection.setRemoteDescription(rtcSessionDescription);
    }
  });
  // Listening for remote session description above

  // Listen for remote ICE candidates below
  roomRef.collection(COLLECTIONS.CALLEECANDIDATES).onSnapshot((snapshot) => {
    snapshot.docChanges().forEach(async (change) => {
      if (change.type === 'added') {
        const data = change.doc.data();
        console.log(`Got new remote ICE candidate: ${JSON.stringify(data)}`);
        await peerConnection.addIceCandidate(new RTCIceCandidate(data));
      }
    });
  });
  // Listen for remote ICE candidates above

  store.peerConnection.set(peerConnection);

  return roomRef.id;
};

export const roomExists = async (roomId: string) => {
  const roomRef = db.rooms.doc(roomId);
  const roomSnapshot = await roomRef.get();
  return roomSnapshot.exists;
};

export const joinRoomById = async (roomId: string, store: StoreType) => {
  const roomRef = db.rooms.doc(`${roomId}`);
  const roomSnapshot = await roomRef.get();
  console.log('Got room:', roomSnapshot.exists);

  if (roomSnapshot.exists) {
    console.log('Create PeerConnection with configuration: ', configuration);
    const newPeerConnection = new RTCPeerConnection(configuration);
    const localStream = store.localStream.get;
    if (localStream) {
      localStream.getTracks().forEach((track) => {
        newPeerConnection.addTrack(track, localStream);
      });
    }

    // Code for collecting ICE candidates below
    const calleeCandidatesCollection = roomRef.collection(COLLECTIONS.CALLEECANDIDATES);
    newPeerConnection.addEventListener('icecandidate', (event) => {
      if (!event.candidate) {
        console.log('Got final candidate!');
        return;
      }
      console.log('Got candidate: ', event.candidate);
      calleeCandidatesCollection.add(event.candidate.toJSON());
    });
    // Code for collecting ICE candidates above

    newPeerConnection.addEventListener('track', (event) => {
      console.log('Got remote track:', event.streams[0]);
      event.streams[0].getTracks().forEach((track) => {
        if (store.remoteStream.get) {
          console.log('Add a track to the remoteStream:', track);
          store.remoteStream.get.addTrack(track);
        }
      });
    });

    // Code for creating SDP answer below
    const room = roomSnapshot.data();
    if (room) {
      console.log('Got offer:', room.offer);
      store.remoteName.set(room.offer.name);
      store.callStart.set(new Date());
      await newPeerConnection.setRemoteDescription(new RTCSessionDescription(room.offer));
    }
    const answer = await newPeerConnection.createAnswer();
    console.log('Created answer:', answer);
    await newPeerConnection.setLocalDescription(answer);

    const name = store.name.get || '-';
    const roomWithAnswer = {
      answer: {
        type: answer.type,
        sdp: answer.sdp,
        name: name,
      },
    };
    await roomRef.update(roomWithAnswer);
    // Code for creating SDP answer above

    // Listening for remote ICE candidates below
    roomRef.collection(COLLECTIONS.CALLERCANDIDATES).onSnapshot((snapshot) => {
      snapshot.docChanges().forEach(async (change) => {
        if (change.type === 'added') {
          const data = change.doc.data();
          console.log(`Got new remote ICE candidate: ${JSON.stringify(data)}`);
          await newPeerConnection.addIceCandidate(new RTCIceCandidate(data));
        }
      });
    });
    // Listening for remote ICE candidates above

    store.peerConnection.set(newPeerConnection);
  }
};

export const hangUp = async (store: StoreType) => {
  if (store.remoteStream.get) {
    store.remoteStream.get.getTracks().forEach((track) => track.stop());
  }
  if (store.peerConnection.get) {
    store.peerConnection.get.close();
  }

  // Delete room on hangup
  if (store.roomId.get) {
    const roomRef = db.rooms.doc(store.roomId.get);
    const [calleeCandidates, callerCandidates] = await Promise.all([
      roomRef.collection(COLLECTIONS.CALLEECANDIDATES).get(),
      roomRef.collection(COLLECTIONS.CALLERCANDIDATES).get(),
    ]);
    await Promise.all([
      Promise.all(calleeCandidates.docs.map((candidate) => candidate.ref.delete())),
      Promise.all(callerCandidates.docs.map((candidate) => candidate.ref.delete())),
      roomRef.delete(),
    ]);
  }

  window.location.href = URLS.landing;
};
