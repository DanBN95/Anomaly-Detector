package test;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;


public class TimeSeries {

	private HashMap<String,Vector> hashMap;
	
	public TimeSeries(String csvFileName) {

		String line="";
		double temp;
		int j=0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFileName));
			String [] features=line.split(",");
			this.hashMap=new HashMap<String,Vector>();
			int colSize= features.length;
			while((line=br.readLine())!=null) {
				String[] stringsValues = line.split(",");
				Vector doubleValues=new Vector();
				for (int i = 0; i < stringsValues.length; i++) {
					temp = Double.parseDouble(stringsValues[i]);
					doubleValues.add(temp);
				}
				hashMap.put(stringsValues[j],doubleValues);
				j++;
			}

		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e){
		e.printStackTrace();}
	}
public HashMap<String,Vector> getHashMap() {return this.hashMap;} //returns pointer to csv

public String[] FeaturesList() { //return Keys of csv
		String[] keys=this.hashMap.keySet().toArray(new String[hashMap.size()]);
		return keys;
}
public static void addFeature(){

}
public void addRow(String row){
	String[] stringsValues = row.split(",");
	for(this.hashMap.)
}
public void readCSVfromFile(File f) {

}
}
