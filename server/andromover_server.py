import socket
import argparse
from typing import Callable, Optional

from andromover_mouse import mouse


BACKLOG = 0
PACKET_PREAMBLE = 4
RECV_SIZE = 1024


def echo(msg):
    print(msg)
    return msg.upper()


def recv(sock: socket.socket):
    preamble_size = PACKET_PREAMBLE
    preamble = b''
    while preamble_size:
        preamble_part = sock.recv(preamble_size)
        if preamble_part:
            preamble += preamble_part
            preamble_size -= len(preamble_part)
        else:
            return preamble_part

    msg_size = int.from_bytes(preamble, byteorder='big')
    buffer = bytearray(msg_size)
    mv = memoryview(buffer)
    while msg_size:
        nbytes = sock.recv_into(mv, msg_size)
        msg_size -= nbytes
        mv = mv[nbytes:]
    return buffer


def serve(address: str, port: int, *, bluetooth: bool = False,
          keepalive: Optional[float] = 30, preamble: bool = False,
          callback: Callable[[bytes], Optional[bytes]] = echo):

    if bluetooth:
        server_sock = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM,
                                    socket.BTPROTO_RFCOMM)
    else:
        server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    server_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
    server_sock.bind((address, port))
    server_sock.listen(BACKLOG)

    recv_fun = recv if preamble else lambda sock: sock.recv(RECV_SIZE)

    try:
        with server_sock:
            while True:
                client_sock, client_addr = server_sock.accept()
                client_sock.settimeout(keepalive)
                print('Started connection with: {}'.format(client_addr))
                try:
                    while True:
                        data = recv_fun(client_sock)
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
    except KeyboardInterrupt:
        print('Server shutdown')
    finally:
        server_sock.close()


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
    parser.add_argument('--preamble', action='store_true',
                        help='If specified, the first 4 bytes of every msg are'
                             'considered to be the length of the rest of the msg')
    args = parser.parse_args()

    serve(args.address, args.port, bluetooth=args.bluetooth,
          keepalive=args.keepalive, preamble=args.preamble,
          callback=mouse)
