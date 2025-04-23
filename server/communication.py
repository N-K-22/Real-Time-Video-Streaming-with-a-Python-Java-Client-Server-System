import socketio
import datetime as DATE
import asyncio
import aiohttp
import threading
import socket
#used python-socketio.readthedocs.io/en/v4/server.html to figure out whether to use coroutines and become more familiar with the library
if __debug__: # if i want to put into debug by default, remove by compiling with -O flag
    #resoruce for this:https://www.reddit.com/r/learnpython/comments/tolt28/are_preprocessor_directives_available_in_python/
    server = socketio.AsyncServer(async_mode='aiohttp',logger=True, engineio_logger=True)
else:
    server = socketio.AsyncServer(async_mode='aiohttp')  #initilizes the thread mode to be true

def UDP_transfer():
    SOCKET.settimeout(15)
    connection_result = SOCKET.bind((IP,PORT))
    if connection_result != 0:
        print("Connection is not possible over UDP with the socket")
        return
    while True:
        data, discard = SOCKET.recvfrom(4096) #are we able to assume this value?
        server.emit('feedback', {"timestamp":DATE.now(), "frame_size":len(data)})
       # my_event()


SOCKET = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

#application = socketio.ASGIApp(server) #not needed for implementation
application = server.Application() #based off of this resource: https://python-socketio.readthedocs.io/en/latest/server.html#id6
server.attach(application)

@server.event
def connect(sid,environ): #socket connected
    print("Connected: ", sid)
    #return

@server.event
async def disconnect(sid): #socket disconnected
    print("Disconnected: ", sid)
    #return

@server.on('feedback') #check syntax for this --> might cause an issue to look into
def my_event(sid,data): 
    server.emit('feedback', {"timestamp":DATE.now(), "frame_size":len(data)}, room=sid)
    return "OK" , 123 #https://stackoverflow.com/questions/7872611/in-python-what-is-the-difference-between-pass-and-return

if __name__ == '__main__':
    #application.run(threaded=True)
    #getting my hands a little wet with python multithreading
    #used this method: https://docs.python.org/3/library/threading.html
    t1 = threading.Thread(target=UDP_transfer)
    t1.start()
    aiohttp.web.run_app(application)
