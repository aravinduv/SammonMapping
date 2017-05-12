/* The following implementation in Java has
 * been ported from the C# code written by
 * Günther M. FOIDL available at
 * https://www.codeproject.com/Articles/43123/Sammon-Projection
 */

import java.util.Random;

public class Helper {
	private static Random rnd = new Random();

	public static double manhattenDistance(double[] vec1, double[] vec2)
	{
		double distance = 0;

		for (int i = 0; i < vec1.length; i++)
			distance += Math.abs(vec1[i] - vec2[i]);

		return distance;
	}
	
	public static void fisherYatesShuffle(int[] arr)
	{
		for (int i = arr.length - 1; i > 0; i--)
		{
			int pos = rnd.nextInt(i + 1);
			int tmp = arr[i];
			arr[i] = arr[pos];
			arr[pos] = tmp;
		}
	}
}
