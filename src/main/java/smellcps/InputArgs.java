package smellcps;

import java.util.List;

public class InputArgs {
	private final String binFilePath;
	private final FuncPrototype funcPrototype;
	private final List<FuncPrototype> shortCircuitFuncPrototypes;

	public InputArgs(String binFilePath, FuncPrototype funcPrototype, List<FuncPrototype> shortCircuitFuncPrototypes) {
		this.binFilePath = binFilePath;
		this.funcPrototype = funcPrototype;
		this.shortCircuitFuncPrototypes = shortCircuitFuncPrototypes;
		
	}
}

class FuncPrototype {
	private final long funcAddr;
	private final String funcName;
	private final String[] varNames;
	private final String[] varCTypes;
	private final String retType;
	public FuncPrototype(long funcAddr, String funcName, String[] varNames, String[] varCTypes, String retType) {
		this.funcAddr = funcAddr;
		this.funcName = funcName;
		this.varNames = varNames;
		this.varCTypes = varCTypes;
		this.retType = retType;
		
	}
}
