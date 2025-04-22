import socketio
import datetime as DATE
import asyncio
import aiohttp
#used python-socketio.readthedocs.io/en/v4/server.html to figure out whether to use coroutines and become more familiar with the library
if __debug__: # if i want to put into debug by default, remove by compiling with -O flag
    #resoruce for this:https://www.reddit.com/r/learnpython/comments/tolt28/are_preprocessor_directives_available_in_python/
    server = socketio.AsyncServer(async_mode='aiohttp',logger=True, engineio_logger=True)
else:
    server = socketio.AsyncServer(async_mode='aiohttp')  #initilizes the thread mode to be true


#application = socketio.ASGIApp(server)
application = server.Application()
server.attach(application)



@server.event
def connect(sid,environ): #socket connected
    print("Connected: ", sid)
    return

@server.event
def disconnect(sid): #socket disconnected
    print("Disconnected: ", sid)
    return

@server.on('feedback') #check syntax for this
def my_event(sid,data):
    server.emit('feedback', {"timestamp":DATE.now(), "frame_size":len(data)}, room=sid)
    return 

if __name__ == '__main__':
    #application.run(threaded=True)
    aiohttp.web.run_app(application)

