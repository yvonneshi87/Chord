# Chord
## 1. Author:<br/>
   Xin Guan<br/>
   Yvonne (Yunyan) Shi<br/>
   Emily (Yunfang) Wang<br/>

## 2. Project Description:<br/>
   This project is an implementation of Chord in Java.<br/>

## 3. Files included:<br/>
   Chord.java<br/>
   Node.java<br/>
   Message.java<br/>
   FingerTableFixing.java<br/>
   PredecessorChecking.java<br/>
   Stabilization.java<br/>
   Listener.java<br/>
   Responder.java<br/>
   Util.java<br/>
   Exceptions/ConnectToNodeException.java<br/>
   Exceptions/FindNodeIsaException.java<br/>

## 4. Instructions for running:<br/>
#### (1)Open terminal and direct yourself to the source code directory by using the command:<br/>
   $ cd Chord/src<br/>
#### (2) Compile code by using the command:<br/>
   $ javac \*.java Exceptions/\*.java<br/>
#### (3) Run the program by using the command:<br/>
   $ java Chord<br/>
#### (4) If you see a dialog as bellows prompting you to pick an option, you have successfully started this application.<br/>
   // Welcome to the Chord P2P network!<br/>
   // Please choose one of the following options:<br/>
   // 1 - Create/Join a Chord Ring<br/>
   // 2 - Check information<br/>
   // 3 - Query<br/>
   // 4 - Stop a node<br/>
   // 5 - Run query simulation<br/>
   // 6 - Exit<br/>

## 5. Reference:<br/>
   Stoica, I., Morris, R., Liben-Nowell, D., Karger, D. R., Kaashoek, M. F., Dabek, F., & Balakrishnan, H. (2003). Chord: a scalable peer-to-peer lookup protocol for internet applications. IEEE/ACM Transactions on networking, 11(1), 17-32.
