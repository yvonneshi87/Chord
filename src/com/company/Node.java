package com.company;

public class Node {
    private static final int M = 3;
    private int id;
    private String ip;
    private int port;
    private int[] fingerTable;
    private int predecessor;
    private int[] successors;

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
    private void join(int insertedId) {
        // id = -1 means null node
        predecessor = -1;
        findSuccessors(id);
    }

    private void stabilize() {
        // implement
    }

    private void findSuccessors(int id) {
        successors = new int[3];
        for (int i = 0; i < 3; i++) {
            successors[i] = findSuccessor(id);
        }
    }
    private void createFingerTable() {
        fingerTable = new int[M];
        // implement
    }

    public int findSuccessor(int id) {
        // implement
        return 0;
    }

    // search the local table for the highest predecessor of id
    public int closestPrecedingNode(int id) {
        return 0;
    }




}
