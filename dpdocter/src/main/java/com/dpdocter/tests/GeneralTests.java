package com.dpdocter.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class GeneralTests {

	public static void main(String args[]){
		int []a = {1, 5, 4, 3, 2};
		GfG g = new GfG();
		System.out.println(g.minSwaps(a));

	}
}
//Java program to find minimum number of swaps
//required to sort an array

class GfG
{
	// Function returns the minimum number of swaps
	// required to sort the array
	public static int minSwaps(int[] arr)
	{
		int n = arr.length;

		// Create two arrays and use as pairs where first
		// array is element and second array
		// is position of first element
		ArrayList <Custom> arrpos =
				new ArrayList <Custom> ();
		for (int i = 0; i < n; i++)
			arrpos.add(new Custom(arr[i], i));

		// Sort the array by array element values to
		// get right position of every element as the
		// elements of second array.
		Collections.sort(arrpos, new Comparator<Custom>()
		{
			@Override
			public int compare(Custom o1,
					Custom o2)
			{
				if (o1.getValue() > o2.getValue())
					return -1;

				// We can change this to make it then look at the
				// words alphabetical order
				else if (o1.getValue().equals(o2.getValue()))
					return 0;

				else
					return 1;
			}
		});
System.out.println(arrpos);
		// To keep track of visited elements. Initialize
		// all elements as not visited or false.
		boolean[] vis = new boolean[n];
		Arrays.fill(vis, false);

		// Initialize result
		int ans = 0;

		// Traverse array elements
		for (int i = 0; i < n; i++)
		{
			// already swapped and corrected or
			// already present at correct pos
			if (vis[i] || arrpos.get(i).getValue() == i)
				{
				System.out.println(i+"continue");
				continue;
				}

			// find out the number of node in
			// this cycle and add in ans
			int cycle_size = 0;
			int j = i;
			while (!vis[j])
			{
				
				for (int k = 0; k < vis.length; k++)System.out.println(vis[k]);
				vis[j] = true;
				System.out.println("i"+i+"........"+"j"+j);
				// move to next node
				j = arrpos.get(j).getValue();
				System.out.println(j);
				cycle_size++;
			}

			// Update answer by adding current cycle.
			ans += (cycle_size - 1);
			System.out.println("cycle_size"+cycle_size+".....ans"+ans);
		}

		// Return result
		return ans;
	}
}

//Driver class
//class MinSwaps
//{
//	// Driver program to test the above function
//	public static void main(String[] args)
//	{
//		int []a = {1, 5, 4, 3, 2};
//		GfG g = new GfG();
//		System.out.println(g.minSwaps(a));
//	}
//}
//This code is contributed by Saksham Seth
