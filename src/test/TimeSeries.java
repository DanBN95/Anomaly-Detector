package test;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class TimeSeries {

	private HashMap<String, float[]> hashMap;
	private int vector_size;

	public TimeSeries(String csvFileName) {

		int i=0;
		boolean eflag=false;
		int feature_index=0;
		int count=1;
		Scanner myScanner=null;
		try {
			myScanner =new Scanner(new BufferedReader(new FileReader(csvFileName)));
			String line = myScanner.nextLine(); // read from csv file the features line
			String [] features=line.split(","); //split the features
			this.hashMap= new HashMap<String, float[]>();
			Vector<Vector<Float>> wholeLines=new Vector<Vector<Float>>();
			for(i=0;i<features.length;i++)

				wholeLines.add(new Vector<>()); //preparing columns for each feature

			i=0; //initialize i
			while(myScanner.hasNextLine()) { //as long file hasn't readed completely

				if(!wholeLines.isEmpty()) {

					String row = myScanner.nextLine();
					String [] vec_by_row = row.split(",");
					//System.out.print("line "+count+":\t");
					for(String s : vec_by_row) {
						if (i == features.length)
							i = 0;
						//System.out.print(s);
						wholeLines.get(i).add(Float.parseFloat(s));
						i++;
					}
					count++;
					//System.out.println();
				}
				else {
					eflag=true;
				}
			}
			if(features.length!=wholeLines.size())
				System.out.println("Warning! size of features is not equal to size of wholeLines!");

			int size = 0;
			for(Vector<Float> v : wholeLines){ //put hash map with Key:Feature, and Vector of its column
				size=v.size();
				float [] featureVec = new float[size];
				for(int j=0;j<size;j++)
					featureVec[j]=v.get(j);
				this.hashMap.put(features[feature_index],featureVec);
				feature_index++;
			}
			setVector_size(size);

			for(Vector<Float> v : wholeLines)
				v.removeAllElements();
			wholeLines.removeAllElements(); //removing since we already have precise float array in hashmap

		} catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(myScanner!=null)
					myScanner.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String,float []> getHashMap() {return this.hashMap;} //returns pointer to csv

	public String[] FeaturesList() { //return Keys of csv
		String[] keys=this.hashMap.keySet().toArray(new String[hashMap.size()]);
		return keys;
	}

	//return the equal size of all vectors
	public int getSizeOfVector() {
		return vector_size;
	}

	//set vector's size
	public void setVector_size(int vector_size) {
		this.vector_size = vector_size;
	}
	//add feature with empty vector
	public void addFeature(String feature) {
		this.hashMap.put(feature,new float[vector_size]);
	}

	public void addRow(String row) {
		Scanner in = new Scanner(row);
		int i = 0;
		in.useDelimiter(",");
		String[] features = FeaturesList();
		for (float[] f : this.hashMap.values()) {
			if (in.hasNext()) {
				float[] temp = new float[this.vector_size + 1];
				for (int j = 0; j < vector_size; j++)
					temp[j] = f[j];
				f = temp;
				f[vector_size] = in.nextFloat();
				this.hashMap.put(features[i], f);
			}
			i++;
		} in.close();
	}

	//returns row array at index line
	public float [] row_array (int index) {
		float [] arr = new float[this.vector_size];
		for(int i=0;i<this.vector_size;i++)
			arr[i] = valueAtIndex(index,FeaturesList()[i]);
		return arr;
	}

	//returns the value at the [timeStep][feature_key] index
	public float valueAtIndex(int timeStep, String feature_key) {
		return this.hashMap.get(feature_key)[timeStep];
	}

}

