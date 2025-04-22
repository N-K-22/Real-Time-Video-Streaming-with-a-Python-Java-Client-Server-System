import socketio

server = socketio.Server()



@server.event
def connect(id,environ): #socket connected
    print("Connected: ", id)


@server.event
def disconnect(id): #socket disconnected
    print("Disconnected: ", id)
