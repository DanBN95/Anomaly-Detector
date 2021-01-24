package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

	List<CorrelatedFeatures> correlatedFeaturesList = new LinkedList<>();

	@Override
	public void learnNormal(TimeSeries ts) {
		float best_correlated = 0;
		float threshold = (float) 0.9;
		String save_through_feature = "";
		String [] features = ts.FeaturesList();
		for (int i=0;i<ts.getHashMap().size()-1;i++) { //for every feature checking cov with the other features
			String feature_check = features[i];
			for (int j=i+1;j<ts.getHashMap().size();j++) {
				String through_feature = features[j];
				float[] v_check = ts.getHashMap().get(feature_check);
				float[] through_v = ts.getHashMap().get(through_feature);
				if (v_check != through_v) {
					if (Math.abs(StatLib.pearson(v_check, through_v)) > best_correlated) {
						best_correlated = Math.abs(StatLib.pearson(v_check, through_v)); //set the best cor
						save_through_feature = through_feature;
					}
				}
			}
			if (best_correlated < threshold)
				continue;
//			if(Cor_Exists(feature_check,save_through_feature))
//				continue;
			Point[] p = new Point[ts.getSizeOfVector()];
			for (int k = 0; k < ts.getSizeOfVector(); k++)
				p[k] = new Point(ts.valueAtIndex(k, feature_check), ts.valueAtIndex(k, save_through_feature));
			Line reg_line = StatLib.linear_reg(p);
			float max_dev = -1;
			for (Point point : p) {
				float result = StatLib.dev(point, reg_line);
				if (result > max_dev)
					max_dev = result;
			}
			max_dev*=(float)1.1;
			this.correlatedFeaturesList.add(new CorrelatedFeatures(feature_check, save_through_feature, best_correlated, reg_line, max_dev));
			best_correlated = 0;
		}
	}


	@Override
	//Input: get TimeSeries object
	//Output: returns list of all the anomalies were detected
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> anomalyReportList = new LinkedList<>();
		String[] features = ts.FeaturesList();
		for (CorrelatedFeatures correlatedFeatures : this.correlatedFeaturesList) {
		for (int i = 0; i < ts.getSizeOfVector(); i++) {
				Point p = new Point(ts.valueAtIndex(i, correlatedFeatures.feature1), ts.valueAtIndex(i, correlatedFeatures.feature2));
				if (StatLib.dev(p, correlatedFeatures.lin_reg) > correlatedFeatures.threshold)
					anomalyReportList.add(new AnomalyReport( correlatedFeatures.feature1 + "-"
							+ correlatedFeatures.feature2, (long)i+1));
				//System.out.println(correlatedFeatures.feature1+"-"+correlatedFeatures.feature2);
			}
		}
		if (anomalyReportList.isEmpty())
			return null;
		else
			return anomalyReportList;
	}

	public List<CorrelatedFeatures> getNormalModel() {
		return this.correlatedFeaturesList;
	}

//	public boolean Cor_Exists(String feature_check, String through_feature) {
//		boolean flag = false;
//		for (CorrelatedFeatures clf : this.correlatedFeaturesList) {
//			if ((clf.feature2 == feature_check) && (clf.feature1 == through_feature))
//				return flag = true;
//		}
//		return flag;
//	}
}
