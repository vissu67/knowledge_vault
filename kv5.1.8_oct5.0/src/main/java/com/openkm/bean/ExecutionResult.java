package com.openkm.bean;

public class ExecutionResult {
	private int exitValue = -1;
	private String stderr;
	private String stdout;
		
	public String getStdout() {
		return stdout;
	}
	
	public void setStdout(String stdout) {
		this.stdout = stdout;
	}
	
	public String getStderr() {
		return stderr;
	}
	
	public void setStderr(String stderr) {
		this.stderr = stderr;
	}
	
	public int getExitValue() {
		return exitValue;
	}
	
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		sb.append("exitValue="); sb.append(exitValue);
		sb.append(", stderr="); sb.append(stderr);
		sb.append(", stdout="); sb.append(stdout);
		sb.append("}");
		return sb.toString();
	}
}
