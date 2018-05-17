import socket
import argparse
from typing import Callable, Optional

from andromover_mouse import Mouse


BACKLOG = 0
BLUETOOTH_PROTO = socket.BTPROTO_RFCOMM


def echo(msg):
    print(msg)
    return msg.upper()


def recv(sock: socket.socket, size: Optional[int] = 4):
    msg_size = int(sock.recv(size))
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
        server_sock = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM,
                                    BLUETOOTH_PROTO)
    else:
        server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    server_sock.bind((address, port))
    server_sock.listen(BACKLOG)

    with server_sock:
        while True:
            client_sock, client_addr = server_sock.accept()
            client_sock.settimeout(keepalive)
            print('Started connection with: {}'.format(client_addr))
            try:
                while True:
                    data = client_sock.recv(1024)
                    if not data:
                        print('Connection closed by peer')
                        break
                    reply = callback(data)
                    if reply:
                        client_sock.send(reply)
            except socket.timeout:
                print('Connection timed out')
            finally:
                client_sock.close()


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('-i', '--address', default='localhost',
                        help='IPv4 address on which to accept connection')
    parser.add_argument('-p', '--port', type=int, default=8080,
                        help='Port on which to accept connection')
    parser.add_argument('-b', '--bluetooth', action='store_true',
                        help='If set, specified address will be considered to '
                             'be of bluetooth family rather than inet (default)')
    parser.add_argument('-k', '--keepalive', type=float, default=None,
                        help='Timeout of inactivity on connection after which '
                             'it is severed (by default it is infinity)')
    args = parser.parse_args()

    serve(args.address, args.port, bluetooth=args.bluetooth,
          keepalive=args.keepalive, callback=Mouse())
