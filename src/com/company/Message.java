package com.company;

import java.net.DatagramSocket;

public class Message {

  enum MessageType {
    FIND_SUCCESSOR, RETURN_PREDECESSOR, NOTIFY, PING
  }

  public static int requestFindSuccessor(int id, Node targetNode) {

  }

  public static int requestReturnPrecessor(Node targetNode) {

  }

  public static void requestNotify(Node selfNode, Node targetNode) {

  }

  public static int requestPing(Node targetNode) {

  }

  public static int receiveIncomingMessage(DatagramSocket socket, Node selfNode) {

  }

}
