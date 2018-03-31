import socket
import argparse
from typing import Callable, Optional


BACKLOG = 0
BLUETOOTH_PROTO = socket.BTPROTO_RFCOMM


def echo(msg):
    print(msg)
    return msg.upper()


def recv(sock: socket.socket, size: Optional[int] = 4):
    msg_size = sock.recv(size)
    buffer = bytearray(msg_size)
    mv = memoryview(buffer)
    while msg_size:
        nbytes = sock.recv_into(mv, msg_size)
        msg_size -= nbytes
        mv = mv[nbytes:]
    return buffer


def serve(address: str, port: int, *,
          bluetooth: bool = False, keepalive: Optional[float] = 30,
          callback: Callable[[bytes], Optional[bytes]] = echo):

    if bluetooth:
        server = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM,
                               BLUETOOTH_PROTO)
    else:
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind((address, port))
    server.listen(BACKLOG)

    with server:
        while True:
            client, addr = server.accept()
            client.settimeout(keepalive)
            print('Started connection with: {}'.format(addr))
            try:
                while True:
                    data = client.recv(1024)
                    if not data:
                        print('Connection closed by peer')
                        break
                    reply = callback(data)
                    if reply:
                        client.send(reply)
            except socket.timeout:
                print('Connection timed out')
            finally:
                client.close()


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('-i', '--address', default='localhost',
                        help='address on which to accept connection')
    parser.add_argument('-p', '--port', type=int, default=8080,
                        help='port on which to accept connection')
    parser.add_argument('-b', '--bluetooth', action='store_true',
                        help='if set, specified address will br considered to '
                             'be of bluetooth family rather than inet (default)')
    parser.add_argument('-k', '--keepalive', type=float, default=None,
                        help='timeout of inactivity on connection after which '
                             'it is severed (by default it is infinity)')
    args = parser.parse_args()

    serve(args.address, args.port, bluetooth=args.bluetooth,
          keepalive=args.keepalive)
