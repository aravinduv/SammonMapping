/* The following implementation in Java has
 * been ported from the C# code written by
 * Günther M. FOIDL available at
 * https://www.codeproject.com/Articles/43123/Sammon-Projection
 */

import java.util.Random;

public class SammonProjection {
	private int maxIteration;
	private double lambda = 1;
	private int[] indicesI;
	private int[] indicesJ;
	
	protected double[][] distanceMatrix;
	public double[][] inpData;
	
	public int count;
	public int outputDimension;
	public double[][] projection;
	public int iteration;
	
	public SammonProjection(double[][] input, int numDims, int maxIter){
		if (input == null || input.length == 0)
			throw new IllegalArgumentException();

		this.inpData = input;
		this.outputDimension = numDims;
		maxIteration = maxIter;
		this.count = this.inpData.length;

		initialize();

		this.indicesI = new int[this.count];
		for(int i=0;i<indicesI.length;i++){
			indicesI[i] = i;
		}
		
		this.indicesJ = indicesI.clone();
	}
	
	private void initialize()
	{
		distanceMatrix = calculateDistanceMatrix();

		Random rnd = new Random();
		double[][] projection = new double[this.count][];
		
		this.projection = projection;
		for (int i = 0; i < projection.length; i++)
		{
			double[] projectionI = new double[this.outputDimension];
			projection[i] = projectionI;
			for (int j = 0; j < projectionI.length; j++)
				projectionI[j] = rnd.nextInt(this.count);
		}
	}
	
	private double[][] calculateDistanceMatrix()
	{
		double[][] distanceMatrix = new double[this.count][];
		double[][] inputData = this.inpData;

		for (int i=0;i<distanceMatrix.length;i++)
		{
			double[] distances = new double[this.count];
			distanceMatrix[i] = distances;

			double[] inputI = inputData[i];

			for (int j = 0; j < distances.length; j++)
			{
				if (j == i)
				{
					distances[j] = 0;
					continue;
				}

				distances[j] = Helper.manhattenDistance(
					inputI,
					inputData[j]);
			}
		}

		return distanceMatrix;
	}
	
	public void createMapping()
	{
		for (int i=maxIteration;i >= 0;i--)
			this.iterate();
	}
	
	public void iterate()
	{
		int[] indI = this.indicesI;
		int[] indJ = this.indicesJ;
		double[][] distMatrix = this.distanceMatrix;
		double[][] projection = this.projection;

		// Shuffle the indices-array for random pick of the points:
		Helper.fisherYatesShuffle(indI);
		Helper.fisherYatesShuffle(indJ);

		for (int i = 0; i < indicesI.length; i++)
		{
			double[] distancesI = distMatrix[indI[i]];
			double[] projectionI = projection[indI[i]];

			for (int j = 0; j < indJ.length; j++)
			{
				if (indI[i] == indJ[j])
					continue;

				double[] projectionJ = projection[indJ[j]];

				double dij = distancesI[indJ[j]];
				double Dij = Helper.manhattenDistance(
						projectionI,
						projectionJ);

				if (Dij == 0)
					Dij = 1e-10;

				double delta = lambda * (dij - Dij) / Dij;

				for (int k = 0; k < projectionJ.length; k++)
				{
					double correction =
						delta * (projectionI[k] - projectionJ[k]);

					projectionI[k] += correction;
					projectionJ[k] -= correction;
				}
			}
		}

		reduceLambda();
	}
	
	private void reduceLambda()
	{
		this.iteration++;

		double ratio = (double)this.iteration / maxIteration;
		lambda = Math.pow(0.01, ratio);
	}
}
