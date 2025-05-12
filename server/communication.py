import socketio
import datetime as DATE
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
    server = socketio.AsyncServer(async_mode='aiohttp')  #initilizes the thread mode to be true

async def UDP_transfer(server): #set up for USP set up
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as SOCKET:
        SOCKET.settimeout(5) #https://stackoverflow.com/questions/19570672/non-blocking-error-when-adding-timeout-to-python-server
        connection_result = SOCKET.bind((IP,PORT))
        SOCKET.setblocking(1) #https://stackoverflow.com/questions/19570672/non-blocking-error-when-adding-timeout-to-python-server 
        while True:
            data, discard = await asyncio.get_event_loop().sock_recvfrom(SOCKET, 65535)
            print(data)
            await server.emit("feedback", {"timestamp":DATE.now(), "frame_size":len(data)})
        close_UDP(SOCKET)
def close_UDP(s): #CLOSE SOCKET
    s.close()



@server.event
async def connect(sid,environ): #socket connected
    print("Connected: ", sid)


@server.event
async def disconnect(sid): #socket disconnected
    print("Disconnected: ", sid)

@server.on("feedback")
async def feedback(sid,data):
    print(f"time stamp: {DATE.now()}, frame_size: {len(data)}")
    server.emit("feedback", {"timestamp":DATE.now(), "frame_size":len(data)}, room=sid)
    return "OK" , 123 #https://stackoverflow.com/questions/7872611/in-python-what-is-the-difference-between-pass-and-return

@server.event 
async def my_event(sid,data): 
    print(f"time stamp: {DATE.now()}, frame_size: {len(data)}")
    server.emit("feedback", {"timestamp":DATE.now(), "frame_size":len(data)}, room=sid)
    return "OK" , 123 #https://stackoverflow.com/questions/7872611/in-python-what-is-the-difference-between-pass-and-return

async def init():

    #getting my hands a little wet with python multithreading
    #used this method: https://docs.python.org/3/library/threading.html
    #application = socketio.ASGIApp(server) #not needed for implementation
    application = web.Application() #based off of this resource: https://python-socketio.readthedocs.io/en/latest/server.html#id6
    server.attach(application)
    print("Creating Asyncio Task for Receiving Data Over UDP")
    task = asyncio.create_task(UDP_transfer(server)) #runs in background
    print("Background UDP transfer set up complete")
    return application

def main():
    application = asyncio.run(init()) #use https://docs.python.org/3/library/asyncio.html
    print("Starting the web server")
    aiohttp.web.run_app(application, port =WEB_PORT,host="0.0.0.0")
    print("Web server closed")

    
if __name__ == '__main__':
   main()