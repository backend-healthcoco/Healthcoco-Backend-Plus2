package com.dpdocter.tests;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.PriorityQueue;

import com.dpdocter.beans.User;


public class GeneralTests {

		public static void main(String[] args) throws UnsupportedEncodingException{

			
			LinkedList linkedList = new LinkedList();
//			linkedList.add(e)
//			Comparator<String> queueComparator = new VowelComparator();
			PriorityQueue<String> priorityQueue = new PriorityQueue<String>();
			priorityQueue.add("orange");
			priorityQueue.add("fig");
			priorityQueue.add("watermelon");
			priorityQueue.add("lemon");
			while (priorityQueue.size() != 0) {
				System.out.println(priorityQueue.remove());
			}
		}
}
