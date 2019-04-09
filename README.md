# ICStream

#### Current Bugs
* Server continues sending packets after client has stopped, i.e. threads don't die.
* "Start stream" multiple times in one client will spawn additional counting threads for it.

#### TODO

###### Client
* Send END packet back to server so it can know to stop sending packets to this client.
* Add alternate request type for streaming (at this point, SampleRequest.java just watches the server).

###### Server
* Assign each client the ID of the server thread from which they're running so the server can keep track of client states.
* Add logic for handling both types of requests (watch & stream).

###### General/Later
* Figure out video/audio integration
* Add checks in server for client START packets to ensure they are expected.

```
if(packet.getData() != "I'm a valid watcher, can I connect?") {
    refuse();
}
```
* Add ability for watchers to see IDs of current streaming clients, send secondary START packet with their viewing choice.
* Refactor in general: get rid of unused vars, etc.