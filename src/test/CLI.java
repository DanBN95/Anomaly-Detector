package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		// example: commands.add(c.new ExampleCommand());
		// implement
		commands.add(c.new UploadCsvFile());
		commands.add(c.new CorrelationThresholdCommand());
		commands.add(c. new DetectAnomaliesCommand());
		commands.add((c. new DisplayResultsCommand()));
		commands.add(c. new AnalyzeResultsCommand());
		commands.add(c. new EndInteractionCommand());
	}
	
	public void start() {
		// implement
		boolean flag=true;
		while(flag) {
			dio.write("Welcome to the Anomaly Detection Server.\n");
			dio.write("Please choose an option:\n");
			int i = 1;
			for (Command c : commands) {
				dio.write(i + ". " + c.description);
				dio.write("\n");
				i++;
			}
			try {
				String line=this.dio.readText();
				if(line.equals(""))
					line= dio.readText();
				int command_number = Integer.parseInt(line);
				commands.get(command_number - 1).execute();
				if(commands.get(command_number-1).getClass() == Commands.EndInteractionCommand.class)
					flag=false;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
