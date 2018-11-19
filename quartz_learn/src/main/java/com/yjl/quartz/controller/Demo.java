package com.yjl.quartz.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {
    static class Node {
        public Node() {
        }

        public Node(int i, int j) {
            this.i = i;
            this.j = j;
        }

        private int i;
        private int j;
        private List<Node> list;

        public List<Node> getList() {
            return list;
        }

        public void setList(List<Node> list) {
            this.list = list;
        }

        @Override
        public boolean equals(Object o) {
            Node node = (Node) o;
            return this.getI() == node.getI() && this.getJ() == node.getJ();
        }

        @Override
        public String toString() {
            return "Node{" +
                    "i=" + i +
                    ", j=" + j +
                    '}';
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }

        private Node previousNode;
        private Node lastNode;

        public Node getPreviousNode() {
            return previousNode;
        }

        public void setPreviousNode(Node previousNode) {
            this.previousNode = previousNode;
        }

        public Node getLastNode() {
            return lastNode;
        }

        public void setLastNode(Node lastNode) {
            this.lastNode = lastNode;
        }

        public Node getCopyNode() {
            return new Node(this.i, this.j);
        }
    }

    public static void main(String[] args) throws Exception {
//        test();
//        test1();
        double a = 100.0;
        double b = 40.0;
        double c = a/b;
        System.err.println(c);
        System.err.println((int)c);
        System.err.println((int)c*2);
    }

    public static void test() {
        Node[][] nodes = new Node[6][7];
        addElement(nodes);
        System.out.println(nodes.length);
        List<List<Node>> allTree = new ArrayList<List<Node>>();
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
//                每棵树的根节点
                List<Node> tree = new ArrayList<Node>();
                allTree.add(tree);
                Node node1 = nodes[i][j].getCopyNode();
                Node node2 = nodes[i][j].getCopyNode();
                Node node3 = nodes[i][j].getCopyNode();
                Node node4 = nodes[i][j].getCopyNode();
                addTree(nodes, node1, 1);
                addTree(nodes, node2, 2);
                addTree(nodes, node3, 3);
                addTree(nodes, node4, 4);

            }
        }
    }
    public static void test1() {
        Node[][] nodes = new Node[6][7];
        addElement(nodes);
        Node node = nodes[3][3].getCopyNode();
        node.setList(new ArrayList<Node>());
        Node node1 = nodes[3][3].getCopyNode();
        Node node2 = nodes[3][3].getCopyNode();
        Node node3 = nodes[3][3].getCopyNode();
        Node node4 = nodes[3][3].getCopyNode();
        addTree(nodes, node1, 1);
        addTree(nodes, node2, 2);
        addTree(nodes, node3, 3);
        addTree(nodes, node4, 4);
        node.getList().addAll(node1.getList());
        node.getList().addAll(node2.getList());
        node.getList().addAll(node3.getList());
        node.getList().addAll(node4.getList());
        iterator(node.getList());
    }

//    遍历节点
    public static void iterator(List<Node> list) {
        for (Node node : list) {
            System.out.println(node);
            iterator(node.getList());
        }
    }

    public static int Recursion(int n) {
        if (n == 1) {
            return 0;
        }
        if (n == 2) {
            return 1;
        }
        return Recursion(n - 1) + Recursion(n - 2);
    }

    public static Node addTree1(Node[][] nodes, Node node) {




        return null;
    }

    //    找出每个节点的所有子节点
    public static Node addTree(Node[][] nodes, Node node, int flag) {
        List<Node> lastNode = new ArrayList<Node>();
        node.setList(lastNode);
//        System.out.println("[" + node.getI() + "][" + node.getJ() + "]: " + node);
        if (node.getI() - 1 >= 0) {
            Node node1 = nodes[node.getI() - 1][node.getJ()];
            Node copyNode = node1.getCopyNode();

            lastNode.add(node1);
            return addTree(nodes, node1, 1);
        }
        if (node.getI() + 1 < 6) {
            Node node2 = nodes[node.getI() + 1][node.getJ()];
            lastNode.add(node2);
            return addTree(nodes, node2, 2);
        }
        if (node.getJ() - 1 >= 0) {
            Node node3 = nodes[node.getJ() - 1][node.getI()];
            lastNode.add(node3);
            return addTree(nodes, node3, 3);
        }
        if (node.getJ() + 1 < 6) {
            Node node4 = nodes[node.getJ() + 1][node.getI()];
            lastNode.add(node4);
            return addTree(nodes, node4, 4);
        }
        return null;
    }

    //循环放入元素
    public static void addElement(Node[][] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                nodes[i][j] = new Node(i, j);
            }
        }
    }
/*
           1       2       3       4       5       6
---------------------------------------------------------------
    0|   {0,0}   {0,1}   {0,2}   {0,3}   {0,4}   {0,5}
    1|   {1,0}   {1,1}   {1,2}   {1,3}   {1,4}   {1,5}
    2|   {2,0}   {2,1}   {2,2}   {2,3}   {2,4}   {2,5}
    3|   {3,0}   {3,1}   {3,2}   {3,3}   {3,4}   {3,5}
    4|   {4,0}   {4,1}   {4,2}   {4,3}   {4,4}   {4,5}
    5|   {5,0}   {5,1}   {5,2}   {5,3}   {5,4}   {5,5}
    6|   {6,0}   {6,1}   {6,2}   {6,3}   {6,4}   {6,5}

 */

}
