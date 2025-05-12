import socketio
from datetime import datetime as DATE
import asyncio
import aiohttp 
from aiohttp import web
import threading
import socket

IP = "127.0.0.1"  # listen on all interfaces
PORT = 12345    # UDP port to listen on
WEB_PORT = 8080  # port for socket.iO web server

#used python-socketio.readthedocs.io/en/v4/server.html to figure out whether to use coroutines and become more familiar with the library
if __debug__: # if i want to put into debug by default, remove by compiling with -O flag
    #resoruce for this:https://www.reddit.com/r/learnpython/comments/tolt28/are_preprocessor_directives_available_in_python/
    server = socketio.AsyncServer(async_mode='aiohttp',logger=True, engineio_logger=True)
else:
    server = socketio.AsyncServer(async_mode='aiohttp',cors_allowed_origins='*')  #initilizes the thread mode to be true
application = web.Application() #based off of this resource: https://python-socketio.readthedocs.io/en/latest/server.html#id6
server.attach(application)
#lines above for server set up


@server.event
async def connect(sid,environ): #socket connected
    print("Connected: ", sid)


@server.event
async def disconnect(sid): #socket disconnected
    print("Disconnected: ", sid)

@server.on("feedback")
async def feedback(sid,data):
    print(f"time stamp: {DATE.now()}, frame_size: {len(data)}")


async def UDP_transfer(): #set up for USP set up
    global server
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as SOCKET: #helps with clean shutdown when starting a socket
        SOCKET.settimeout(5) #https://stackoverflow.com/questions/19570672/non-blocking-error-when-adding-timeout-to-python-server
        connection_result = SOCKET.bind((IP,PORT))
        SOCKET.setblocking(False) #https://stackoverflow.com/questions/19570672/non-blocking-error-when-adding-timeout-to-python-server 
        while True:
            try:
                data, discard = await asyncio.get_event_loop().sock_recvfrom(SOCKET, 65507) #receivng data over socket
                print("Data has been received over UDP")
                await server.emit('feedback', {"timestamp":(DATE.now()).strftime("%c"), "frame_size":len(data)}) #https://www.w3schools.com/python/python_datetime.asp
                #transmitting over SocketIO
            except Exception as e: #debugging purposes
                print("Error Processing Packet. Error Message: " + str(e))
def close_UDP(s): #CLOSE SOCKET
    s.close()



async def init():

    #getting my hands a little wet with python multithreading
    #used this method: https://docs.python.org/3/library/threading.html
    #application = socketio.ASGIApp(server) #not needed for implementation

    print("Creating Asyncio Task for Receiving Data Over UDP")
    asyncio.create_task(UDP_transfer()) #runs in background
    print("Background UDP transfer set up complete")
    return application

def main():

    print("Starting the web server")
    aiohttp.web.run_app(init(),host="0.0.0.0",port =WEB_PORT)
    print("Web server closed")

    
if __name__ == '__main__':
   main()