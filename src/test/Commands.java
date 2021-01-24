package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Commands {

	// Default IO interface
	public interface DefaultIO {
		public String readText();

		public void write(String text);

		public float readVal();

		public void write(float val);
	}

	public void uploadFile(String fileName) {
		try {
			PrintWriter localFile = new PrintWriter(new FileWriter(fileName));
			String line;
			int count = 0;

			while (!(line = dio.readText()).equals("done")) {
				localFile.write(line);
				localFile.write("\n");
				localFile.flush();
				count++;
			}
			localFile.close();
			sharedState.setNumOfLines(count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// the default IO to be used in all commands
	DefaultIO dio;

	public Commands(DefaultIO dio) {
		this.dio = dio;
	}

	// you may add other helper classes here

	// the shared state of all commands
	private class SharedState {

		// implement here whatever you need
		private TimeSeries timeSeriesTrain;
		private TimeSeries timeSeriesTest;
		private float threshold;
		private int numOfLines;
		private List<AnomalyReport> anomalyReportList = new LinkedList<>();

		public SharedState() {
			this.threshold = (float) 0.9;
			this.numOfLines = 0;
		}

		public float getThreshold() {
			return threshold;
		}

		public void setThreshold(float threshold) {
			this.threshold = threshold;
		}

		public TimeSeries getTimeSeriesTrain() {
			return timeSeriesTrain;
		}

		public void setTimeSeriesTrain(String timeSeriesTrain) {
			this.timeSeriesTrain = new TimeSeries(timeSeriesTrain);
		}

		public TimeSeries getTimeSeriesTest() {
			return timeSeriesTest;
		}

		public void setTimeSeriesTest(String timeSeriesTest) {
			this.timeSeriesTest = new TimeSeries(timeSeriesTest);
		}

		public List<AnomalyReport> getAnomalyReportList() {
			return anomalyReportList;
		}

		public void setAnomalyReportList(List<AnomalyReport> anomalyReportList) {
			this.anomalyReportList = anomalyReportList;
		}

		public int getNumOfLines() {
			return numOfLines;
		}

		public void setNumOfLines(int numOfLines) {
			this.numOfLines = numOfLines;
		}
	}

	private SharedState sharedState = new SharedState();

	public SharedState getSharedState() {
		return sharedState;
	}

	// Command abstract class
	public abstract class Command {
		protected String description;

		public Command(String description) {
			this.description = description;
		}

		public abstract void execute();

	}

	public class UploadCsvFile extends Command {

		public UploadCsvFile() {
			super("upload a time series csv file");
		}

		@Override
		public void execute() {
			int local_status;
			int count = 0;
			String line;
			for (local_status = 0; local_status < 2; local_status++) {
				if (local_status == 0) {
					dio.write("Please upload your local train CSV file.\n");
					uploadFile("anomalyTrain.csv");
					sharedState.setTimeSeriesTrain("anomalyTrain.csv");
				} else {
					dio.write("Please upload your local test CSV file.\n");
					uploadFile("anomalyTest.csv");
					sharedState.setTimeSeriesTest("anomalyTest.csv");
				}
				dio.write("Upload complete.\n");
			}
		}
	}


	public class CorrelationThresholdCommand extends Command {

		public CorrelationThresholdCommand() {
			super("algorithm settings");
		}

		@Override
		public void execute() {
			float threshold = sharedState.getThreshold();
			boolean flag;
			do {
				flag = false;
				dio.write("The current correlation threshold is " + threshold + "\n");
				dio.write("Type a new threshold\n");
				threshold = dio.readVal();
				if (threshold < 0 || threshold > 1) {
					dio.write("Please choose a value between 0 and 1\n");
					flag = true;
				} else
					sharedState.setThreshold(threshold);
			} while (flag);
		}
	}

	//Command #3
	public class DetectAnomaliesCommand extends Command {

		public DetectAnomaliesCommand() {
			super("detect anomalies");
		}

		@Override
		public void execute() {
			SimpleAnomalyDetector simpleAnomalyDetector = new SimpleAnomalyDetector();
			simpleAnomalyDetector.learnNormal(sharedState.getTimeSeriesTrain());
			sharedState.setAnomalyReportList(simpleAnomalyDetector.detect(sharedState.getTimeSeriesTest()));
			dio.write("anomaly detection complete.\n");
		}
	}

	public class DisplayResultsCommand extends Command {

		public DisplayResultsCommand() {
			super("display results");
		}

		@Override
		public void execute() {
			for (AnomalyReport anomaly_report : sharedState.getAnomalyReportList())
				dio.write(anomaly_report.timeStep + "\t" + anomaly_report.description + "\n");
			dio.write("Done.\n");
		}
	}

	public class AnalyzeResultsCommand extends Command {

		public AnalyzeResultsCommand() {
			super("upload anomalies and analyze results");
		}

		@Override
		public void execute() {
			dio.write("Please upload your local anomalies file.\n");
			List<Point> ContinuousReportList = new LinkedList<>();
			List<String> saveDiscription = new LinkedList<>();
			int anomalyReportList_size = sharedState.anomalyReportList.size();
			float start_timestep = sharedState.anomalyReportList.get(0).timeStep;
			float end_timestep = -1;
			boolean check_last = false;

			for (int i = 0; i < anomalyReportList_size - 1; i++) {
				check_last = false;
				float timestep = sharedState.anomalyReportList.get(i).timeStep;
				float timestep_next = sharedState.anomalyReportList.get(i + 1).timeStep;
				String discription = sharedState.anomalyReportList.get(i).description;
				String discription_next = sharedState.anomalyReportList.get(i + 1).description;
				if (timestep != timestep_next - 1 || !discription.equals(discription_next)) {
					end_timestep = timestep;
					ContinuousReportList.add(new Point(start_timestep, end_timestep));
					saveDiscription.add(discription);
					end_timestep = start_timestep;
					start_timestep = timestep_next;
					check_last = true;
				}
			}
			if (!check_last)
				end_timestep = sharedState.anomalyReportList.get(anomalyReportList_size - 1).timeStep;

			else
				end_timestep = start_timestep;

			ContinuousReportList.add(new Point(start_timestep, end_timestep));
			saveDiscription.add(sharedState.anomalyReportList.get(anomalyReportList_size - 1).description);

			try {
				/*****************************************************************************************************
				 * order whole anomaly reports from the file into a list of Point(time_Start,time_end)
				 */
				String line;
				List<Point> fileReportList = new LinkedList<>();
				int Negative = sharedState.getNumOfLines() - 1;
				int index = 0, Positive = 0;
				while (!(line = dio.readText()).equals("done")) {
					Positive++;
					String[] reports = line.split(",");
					fileReportList.add(new Point(Float.parseFloat(reports[0]), Float.parseFloat((reports[1]))));
					Negative -= (fileReportList.get(index).y - fileReportList.get(index).x + 1);
				}

				/******************************************************************************
				 * compare ranges between the anomaly file and our  detected anomaly list
				 */
				float FP = 0, TP = 0;
				boolean flag=false;
				for (int i = 0; i < ContinuousReportList.size(); i++) {
					flag = false;
					for (int j = 0; j < fileReportList.size(); j++) {
						if ((ContinuousReportList.get(i).y < fileReportList.get(j).x)
								|| (ContinuousReportList.get(i).x > fileReportList.get(j).y))
							flag=false;
						else {
							TP++;
							flag = true;
							break;
						}
					}
					if (!flag)
						FP++;
					}

				dio.write("Upload complete.\n");
				try {
					TP /= Positive;
					TP = (float) (Math.floor((double) TP * 1000)) / 1000;
					dio.write("True Positive Rate: " + TP + "\n");

					FP /= Negative;
					FP = (float) (Math.floor((double) FP * 1000)) / 1000;
					dio.write("False Positive Rate: " + FP + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class EndInteractionCommand extends Command {

		public EndInteractionCommand() {
			super("exit");
		}

		@Override
		public void execute() {}
	}
}
