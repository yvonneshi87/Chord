Author:  Xin Guan
         Yvonne (Yunyan) Shi
         Emily (Yunfang) Wang

Target: Implemente Chord

Reference: Chord: A Scalable Peer-to-peer Lookup Service for Internet
           Applications
           Ion Stoica
           , Robert Morris, David Karger, M. Frans Kaashoek, Hari Balakrishnan 
           MIT Laboratory for Computer Science
           chord@lcs.mit.edu
           http://pdos.lcs.mit.edu/chord


Files included: Chord.java,  Node.java,  Message.java,
                FingerTableFixing.java,  PredecessorChecking.java,  Stablization.java
                Listener.java,  Responder.java,  Util.java
                Exceptions(ConnectToNodeException.java, FindNodeIsaException.java)


Compile and Run the codes:
            # In ther terminal, go to the codes' location
                $ cd Chord/src
            # Compile
                $ javac *.java Exceptions/*.java
            # run code
                $ java Chord
            # The terminal will show you the instructions for you to interact with the system.

            Welcome to the Chord P2P network!
            Please choose one of the following options:
            1 - Create/Join a Chord Ring
            2 - Check information
            3 - Query
            4 - Stop a node
            5 - Run query simulation
            6 - Exit




