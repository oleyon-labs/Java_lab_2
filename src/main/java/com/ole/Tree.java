package com.ole;

public class Tree<T> {
    Tree(){

    }

    public class Node<T> {

        public Node<T> parent;
        public Node<T> leftChild;
        public Node<T> rightChild;
        public T data;

        Node(T value){
            data = value;
        }
    }
}
