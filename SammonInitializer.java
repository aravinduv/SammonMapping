/* The following implementation in Java has
 * been ported from the C# code written by
 * Günther M. FOIDL available at
 * https://www.codeproject.com/Articles/43123/Sammon-Projection
 */

import java.io.BufferedReader;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class SammonInitializer extends JFrame{
	private static int INPUT_DIMENSION = 4;
	private static String FILE = "iris_data.csv";
	private static double[][] inputData;
	private SammonProjection _sammon;
	
	public SammonInitializer() {
		readData();
    	SammonProjection projection = new SammonProjection(inputData,2,1000);
		projection.createMapping();
		_sammon = projection;
		
		initUI();
    }

    private void initUI() {
    	
    	SammonProjectionPostProcess processing = new SammonProjectionPostProcess(_sammon);
        add(processing);

        pack();
        
        setTitle("Sammon Mapping");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
	
	public static void readData(){
		List<double[]> lines = new ArrayList<double[]>();
		String thisLine;
		try {
			BufferedReader br = new BufferedReader(new FileReader(FILE));
			br.readLine();
			while ((thisLine = br.readLine()) != null) {
			     String[] cols = thisLine.split(";");
			     double[] input = new double[INPUT_DIMENSION];
					for (int i = 0; i < INPUT_DIMENSION; i++)
						input[i] = Double.parseDouble(cols[i + 1]);
					lines.add(input);
			}
			
			inputData = lines.toArray(new double[lines.size()][lines.size()]);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	private static void createProjection(){
//		Logic to export the data points
//		Gson gson = new Gson();
//		String json = gson.toJson(projection.projection);
//		System.out.println(json);
//		processing.CreateImage(300, 300, labels, color);
//	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	SammonInitializer ex = new SammonInitializer();
                ex.setVisible(true);
            }
        });
	}
}

class SammonProjectionPostProcess extends JPanel{
	
	private SammonProjection _sammon;
	private int _pointSize = 4;
	private int _fontSize = 8;
	private Color _backGroundColor = Color.WHITE;
	private BufferedImage bmp;
	
	public SammonProjectionPostProcess(SammonProjection sammon)
	{
		if (sammon == null)
			throw new IllegalArgumentException();

		if (sammon.outputDimension != 2)
			throw new IllegalArgumentException();
		
		_sammon = sammon;
		
		setSize();
	}
	
	private void setSize() {
        Dimension d = new Dimension();
        d.width = 400;
        d.height = 400;
        setPreferredSize(d);        
    }
	
	@Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        
        Color[] color = new Color[150];
		String[] labels = new String[150];
		for (int i = 0; i < 50; i++)
		{
			color[i] = Color.RED;
			labels[i] = "set";
		}
		for (int i = 50; i < 100; i++)
		{
			color[i] = Color.GREEN;
			labels[i] = "vers";
		}
		for (int i = 100; i < 150; i++)
		{
			color[i] = Color.BLUE;
			labels[i] = "virg";
		}
		
        doDrawing(g,300,300,labels,color);
    }
	
	public void doDrawing(Graphics g,int width,int height,String[] labels,Color[] colors)
	{
		bmp = new BufferedImage(width, height,BufferedImage.TYPE_BYTE_INDEXED);
		Graphics2D g2d = (Graphics2D)g;
		
		double minX = _sammon.projection[0][0];
		for(int i=1;i<_sammon.projection.length;i++){
			if(_sammon.projection[i][0] < minX){
				minX = _sammon.projection[i][0];
			}
		}
		
		double maxX = _sammon.projection[0][0];
		for(int i=1;i<_sammon.projection.length;i++){
			if(_sammon.projection[i][0] > maxX){
				maxX = _sammon.projection[i][0];
			}
		}
		
		double minY = _sammon.projection[0][1];
		for(int i=1;i<_sammon.projection.length;i++){
			if(_sammon.projection[i][1] < minY){
				minY = _sammon.projection[i][1];
			}
		}
		
		double maxY = _sammon.projection[0][1];
		for(int i=1;i<_sammon.projection.length;i++){
			if(_sammon.projection[i][1] > maxY){
				maxY = _sammon.projection[i][1];
			}
		}

		double ratioX = (width - 20) / (maxX - minX);
		double ratioY = (height - 20) / (maxY - minY);

		Font f = new Font("Arial", Font.PLAIN, _fontSize);
		g2d.setFont(f);
		
		g2d.setBackground(_backGroundColor);

		double[][] projection = _sammon.projection;
		for (int i = 0; i < projection.length; i++)
		{
			if(i<50){
				g2d.setColor(Color.RED);
			}else if(i < 100){
				g2d.setColor(Color.GREEN);
			}else if(i < 150){
				g2d.setColor(Color.BLUE);
			}
			
			double[] projectionI = projection[i];
			double x = projectionI[0];
			double y = projectionI[1];

			x = (x - minX) * ratioX + 10;
			y = (y - minY) * ratioY + 10;

			if (labels != null)
			{
				FontMetrics metrics = g2d.getFontMetrics(f);
				
				g2d.drawString(
					labels[i],
					(float)(x - _pointSize / 2d),
					(float)(y - _pointSize / 2d - metrics.getHeight()));
			}
		}

		g2d.dispose();
	}
}
