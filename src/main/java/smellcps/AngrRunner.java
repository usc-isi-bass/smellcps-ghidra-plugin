package smellcps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;

import com.google.gson.Gson;

public class AngrRunner {

	private final String python_path;
	private final String script_path;

	private final String elf_path;
	private final InputArgs inputArgs;

	private Process process;

	public AngrRunner(String elf_path, InputArgs inputArgs) {
		Properties config = new Properties();
		String python_path = null;
		String script_path = null;
		try {
			FileInputStream propertiesInputStream = new FileInputStream(Paths.get(System.getProperty("user.home"), ".ghidra", "smellcps_plugin_config.properties").toString());
			config.load(propertiesInputStream);
			python_path = config.getProperty("python3");
			script_path = config.getProperty("driver");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
		this.python_path = python_path;
		this.script_path = script_path;
		this.elf_path = elf_path;
		this.inputArgs = inputArgs;
	}

	public String run() throws PythonScriptException {
		System.out.println("AngrRunner.run() start");
		StringBuilder outputSb = new StringBuilder();
		StringBuilder errorSb = new StringBuilder();
		Gson gson = new Gson();
		String inputArgsJsonStr = gson.toJson(this.inputArgs);
		//String inputArgsJsonStr = json.toString(this.inputArgs);
		System.out.println(inputArgsJsonStr);
		try {
			ProcessBuilder pb = new ProcessBuilder(python_path, script_path, this.elf_path, inputArgsJsonStr);
			this.process = pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			while ((line = error.readLine()) != null) {
				errorSb.append(line + "\n");
				System.out.println("ERR: " + line);
			}
			System.out.println("Reading from stdin");
			while ((line = input.readLine()) != null) {
				outputSb.append(line + "\n");
				System.out.println("OUT: " + line);
			}
			System.out.println("Done reading from stdin");

			process.waitFor();
			if (process.exitValue() != 0) {
				throw new PythonScriptException(errorSb.toString());
			}
			System.out.println("AngrRunner.run() check5");
			input.close();
			error.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputSb.toString();
	}
	public void stop() {
		process.destroy();
	}

}
