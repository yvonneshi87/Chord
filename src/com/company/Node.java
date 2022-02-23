package com.company;

public class Node {
    private static final int M = 3;
    private int id;
    private String ip;
    private int port;
    private Node[] fingerTable;
    private Node predecessor;
    private Node[] successors;

    public Node(String port, String ip, int insertedId) {
        setId();
        join(insertedId);
        createFingerTable();
    }


    private void setId() {
        // hash port + ip to 160 bit String
        // String 20 characters
        // truncated to m bits
        // peer id between 0 - (2^m - 1)
        // id =
    }

    // join a Chord ring containing node n'
    private void join(Node existingNode) {
        // id = -1 means null node
        predecessor = null;
        findSuccessors(id);
    }

    private void stabilize() {
        // implement
    }

    private void findSuccessors(int id) {
        successors = new Node[3];
        for (int i = 0; i < 3; i++) {
            successors[i] = findSuccessor(id);
        }
    }
    private void createFingerTable() {
        fingerTable = new Node[M];
        // implement
    }

    public Node findSuccessor(int id) {
        // implement
        return null;
    }

    // search the local table for the highest predecessor of id
    public Node closestPrecedingNode(int id) {
        return null;
    }
}
