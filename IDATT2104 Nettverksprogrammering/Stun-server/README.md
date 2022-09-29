# IDATT2104-Project
Created by: Hermann Owren Elton, Martin Slind Hagen and Olaf Rosendahl

## Introduction
This was an optional assignment in the course IDATT2104 at NTNU.  
The task was to create a STUN server and a P2P application. The P2P application will use the STUN server to get it's IP address so it can communicate with it's counter peer.  

## Server
Action workflows for CI/CD:
- Deployment of Stun-server to Azure VM:
  - https://github.com/olros/IDATT2104-Project/actions/workflows/master_vm_stun-app.yml
- Deployment of frontend demoapplication to Firebase hosting:
  - https://github.com/olros/IDATT2104-Project/actions/workflows/deploy.yml
## Implementation and functionality

*Server*:  
 The stun server is implemented with UDP, TCP and TLS support and all off the different protocols run in their own thread with an event-loop for request handling. The event loop contains a function called post_after which kind of works like async await i JavaScript. For each protocol a connection is either established by receiving a message(UDP) or by accepting (TCP and TLS). When a connection is established it is passed on to the post_after function, so that the listening socket can keep on recieiving more requests. The post_after function has two parameters. The first is the function to await being posted to the event loop, and the second is the function that is to be completed before the first function is posted to the event loop. In the second function the buinsess logic is passed (handling of the data), and this is run in it's own thread. Meanwhile the first function is responisble for sending back the answer to the client. Handling the data in it's own thread means that we can compute the the IP-adress of the client more efficiently. By posting the first function to the event loop the need for locking the socket is not needed, since the event loop only consists of one thread. For TCP connections the listen function is used, where a backlog equal to 5 is passed, meaning that the socket can have up to 5 waiting connections before incoming connections are turned down. Since UDP and TCP packages contain different header fields the listening socket for TCP and UDP can be run on the same port. To handle stun request and responses, the server has a request builder class that verifies stun requests, and creates STUN success and error responses.The builder verifies if the stun requests first to bits are 0, if it includes the magic cookie, and that its type is of a binding request. The server accepts UDP and TCP requests on port 3478 and TLS on port 5349. The STUN responses are created by using structs like they are a object of a class. The struct is set up to look like the STUN response we want to use, and then the different attributes are set, and returned back to the client. 

*Frontend*:  
The frontend application is written in React with Typescript. It uses the Firebase Firestore NoSQL-database to handle connection between clients. When users opens the site, they are presented a screen where they can type in their name and create a "Room". When they create a room, the app gets the user's WebRTC local description which is uploaded to a Firestore document with the roomId. In addition, the user's ICE-candidates (candidates for connection from another user to this user) is uploaded to a subcollection. When the room have been created, the user can share the url with the roomId to others who can open the website and then join the video-chat. On join they upload their own localdescription and ICE candidates to allow to room-creator to connect to the user who joined.

## Future work
## *Server*:
### IPv6
For future work the server needs to be able to handle IPv6 IP adresses. The reason it does not handle it is because the struct we use to handle a client, isnt big enough to hold a client with a IPv6 IP. To solve this we'll need to change the struct that is being used and check if the IP that the client has is of type IPv6 or IPv4. This would mean a huge refactor in how we create STUN responses, which is why it isn't implemented yet. A solution could be to use a framework like [Boost-asio](https://www.boost.org/doc/libs/1_75_0/doc/html/boost_asio.html), which would handle a lot of code for reciving and sending, requests and responses. The git branch feat/all-ip-support has an work in progress solution to handle this problem.

### Addaptive request building
A future improvment would be to change the length of the STUN response and the XOR attribute length(and any other attribute), according the size of the current response being made, and add the padding needed to fill out the response. Also checking and copying/handling requests with specific attributes.

### Add more attributes 
This goes hand in hand with a more adaptive request building. The stun server currently does not handle a request with attributes, like username or password. So a improvment would be to check for different attributes and then adding them to the response being built.  

## *frontend*:

### Full Safari support
The app is a bit buggy on Safari.

### Recover audio after sharing screen
After sharing screen we lose the audio stream of the person sharing the screen. 

## Requirements
*Server:*
- g++10 or later
- Docker
- Docker-compose
- OpenSSL

*Frontend:*
- Node.js
- Firebase
- Typescript

## Dependencies
*Server:*
- g++10 or later
  - GNU C++ Compiler (g++) is a compiler in Linux which is used to compile C++ programs. We run the server in C++20 with this compiler.
- Docker
  - Docker is used to build and run the project. We use Docker because everyone working on the project have different working environments, and Docker is therefore used to ensure that everyone have the same working environment when developing.
- Docker-compose 
  - Docker-compose is used to easily build and run different Docker images
- OpenSSL
  - OpenSSL is used to create and run the TLS part of the sever it creates rsaKeys for TLS communication

*Frontend:*
- Node.js
  - Node.js is required to run the React demoapplication.
- Firebase
  - Firebase Firestore is used as a database to ease the client exchange of LocalDescription which is needed to communicate with WebRTC. Firestore is a simple NoSQL database with a simple SDK which is really quick to setup and get started.
- Typescript
  - Typescript is used instead of Javascript to get typings which is really useful especially when creating large web-applications. Even though this application isn't that large, it's helpful to avoid errors and ease development with intellisense.
    
## Installation instructions
To run the sever and tests you will need [Docker](https://docs.docker.com/engine/install/) and [Docker-compose](https://docs.docker.com/compose/install/). Optionally you can use [GNU Make](https://www.gnu.org/software/make/) to run commads to start the server. If you want to run with TLS you will also need [openSSL](https://www.openssl.org/source/)
## Starting the server

To run the server
without TLS support:
```
git clone https://github.com/olros/IDATT2104-Project.git  
cd IDATT2104-Project/stunserver

# With make:
make run

# Without make:
docker compose build server
docker compose up server
```
With TLS support:
```
git clone https://github.com/olros/IDATT2104-Project.git  
cd IDATT2104-Project/stunserver

# With make:
make genKeys
make run

# Without make:
openssl genrsa -out key.pem 3072
openssl req -new -x509 -key key.pem -out cert.pem -days 365
docker compose build server
docker compose up server
```
## Running tests
```
cd IDATT2104-Project/stunserver

# With make:
make test

# Without make:
docker-compose build test
docker-compose up test
```

## Starting the frontend
```
cd IDATT2104-Project/frontend

# Install dependencies:
yarn

# Run the application:
yarn start
```

## Link to eventual api
- [libssl](https://cppget.org/libssl)
    - This is a library  that provids SSLv3 and TLS implementations for C and C++
- [ReactJS](https://reactjs.org/docs/getting-started.html)(Framework)
    - A JavaScript library for building user interfaces

## Resources
* [UDP server in C](https://www.geeksforgeeks.org/udp-server-client-implementation-c/)
* [RFC documentation for STUN](https://tools.ietf.org/html/rfc5389)
* [The STUN Protocol Explained](https://www.3cx.com/blog/voip-howto/stun-details/)
* [XORing adress](https://docs.microsoft.com/en-us/openspecs/office_protocols/ms-turn/d6f3f10a-b5f2-423a-af1d-a1d69b09ddab)
* [STUN server on Wikipedia](https://en.wikipedia.org/wiki/STUN)
* [IBM accept IPv4 and IPv6](https://www.ibm.com/support/knowledgecenter/ssw_ibm_i_72/rzab6/xacceptboth.htm)
* [Older TLS server in C](https://wiki.openssl.org/index.php/Simple_TLS_Server)
* [Newer TLS server in C](https://aticleworld.com/ssl-server-client-using-openssl-in-c/)
* [Beej guide to network programming](https://beej.us/guide/bgnet/html/)
* [Creating rsa keys and certificate](https://www.scottbrady91.com/OpenSSL/Creating-RSA-Keys-using-OpenSSL)  
* [CPP structs](https://www.cplusplus.com/doc/tutorial/structures/)
