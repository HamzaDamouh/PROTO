import socket
import sys

def send_request(filename, host='172.31.250.176', port=45065):
    with open(filename, 'rb') as f:
        data = f.read()

    with socket.create_connection((host, port), timeout=5) as sock:
        sock.sendall(data)

        resp = b''
        while True:
            chunk = sock.recv(4096)
            if not chunk:
                break
            resp += chunk

    print(resp.decode('utf-8'))

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python tcp_request.py <request-json-file>")
        sys.exit(1)
    send_request(sys.argv[1])
