FROM ubuntu:20.04

RUN apt-get update
RUN apt-get install g++-10 -y
RUN apt-get install openssl -y
RUN apt-get install libssl-dev -y
COPY . .
ENV UDPPORT 3478
ENV TLSPORT 5349
RUN g++-10 -std=c++20 -o main main.cpp -pthread -lssl -lcrypto
CMD ["./main"]
EXPOSE ${UDPPORT}
EXPOSE ${UDPPORT}/udp
EXPOSE ${TLSPORT}