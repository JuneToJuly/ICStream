# ICStream

#### Client Logic/Roadmap
StreamClient
* Client type that is broadcasting/streaming.
* GUI: ????
* When this type of client is launched, it sends a request to the server to stream. (StartStreamRequest id = 100)
* It must provide a name (string).
* The server stores a streamerMap<ThreadID, name>, and immediately checks if the name is unique.
* If unique, the server assigns a thread from its streamerPool to the client, and stores that pair in its map.
* If not unique, responds with appropriate correction/suggestion.
* Once name is valid, connects to socket and begins sending file chunks.

WatchClient
* Client type that is consuming another broadcast.
* GUI: Section to select from connected StreamClients, MediaPlayer for once connected
* First request: connect to server, provide unique name (CurrentStreamsRequest id = 200)
* Response: Checks if name is unique, if so provides list of currently streaming StreamClients
* Second Request: choose StreamClient to watch (ViewStreamRequest id = 201)
* Response: begin broadcasting chosen StreamClient to MediaPlayer

Notes
* So, the server will contain two ThreadPools and two maps for the names of the clients corresponding to active threads in each pool
