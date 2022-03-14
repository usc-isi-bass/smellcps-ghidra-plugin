/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smellcps;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import docking.ActionContext;
import docking.ComponentProvider;
import docking.DockableComponent;
import docking.DockableHeader;
import docking.DockingFrame;
import docking.action.DockingAction;
import docking.action.ToolBarData;
import ghidra.app.ExamplesPluginPackage;
import ghidra.app.decompiler.DecompInterface;
import ghidra.app.decompiler.DecompileOptions;
import ghidra.app.decompiler.DecompileResults;
import ghidra.app.decompiler.DecompiledFunction;
import ghidra.app.plugin.PluginCategoryNames;
import ghidra.app.plugin.ProgramPlugin;
import ghidra.framework.plugintool.*;
import ghidra.framework.plugintool.util.PluginStatus;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSetView;
import ghidra.program.model.data.DataType;
import ghidra.program.model.data.ParameterDefinition;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.FunctionManager;
import ghidra.program.model.listing.FunctionSignature;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.Program;
import ghidra.program.model.listing.VariableStorage;
import ghidra.program.model.pcode.FunctionPrototype;
import ghidra.program.model.pcode.HighFunction;
import ghidra.program.model.pcode.HighParam;
import ghidra.program.model.pcode.HighSymbol;
import ghidra.program.model.symbol.FlowType;
import ghidra.util.HelpLocation;
import ghidra.util.Msg;
import ghidra.util.task.TaskMonitor;
import resources.Icons;
import generic.json.*;

/**
 * TODO: Provide class-level documentation that describes what this plugin does.
 */
//@formatter:off
@PluginInfo(status = PluginStatus.STABLE, packageName = ExamplesPluginPackage.NAME, category = PluginCategoryNames.EXAMPLES, shortDescription = "Plugin short description goes here.", description = "Plugin long description goes here.")
//@formatter:on
public class SmellCpsPlugin extends ProgramPlugin {

	MyProvider provider;
	AngrRunner angrRunner;

	/**
	 * Plugin constructor.
	 *
	 * @param tool The plugin tool that this plugin is added to.
	 */
	public SmellCpsPlugin(PluginTool tool) {
		super(tool, true, true);

		// TODO: Customize provider (or remove if a provider is not desired)
		String pluginName = getName();
		provider = new MyProvider(this, pluginName);

		// TODO: Customize help (or remove if help is not desired)
		String topicName = this.getClass().getPackage().getName();
		String anchorName = "HelpAnchor";
		provider.setHelpLocation(new HelpLocation(topicName, anchorName));
	}

	@Override
	public void init() {
		super.init();

		// TODO: Acquire services if necessary
	}

	@Override
	protected void programActivated(Program p) {
		System.out.println("Active program changed to: " + p.getExecutablePath());
		provider.setProgram(p);
	}

	// TODO: If provider is desired, it is recommended to move it to its own file
	private class MyProvider extends ComponentProvider {
		Program program;
		DecompInterface decompInterface;
		private JPanel panel;
		private DockingAction action;

		public MyProvider(Plugin plugin, String owner) {
			super(plugin.getTool(), owner, owner);
			this.decompInterface = new DecompInterface();
			DecompileOptions decompileOptions = new DecompileOptions();
			this.decompInterface.setOptions(decompileOptions);
			buildPanel();
			createActions();
		}

		public void setProgram(Program program) {
			this.program = program;
			this.decompInterface.openProgram(program);
		}

		// Customize GUI
		private void buildPanel() {
			panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			// JTextArea textArea = new JTextArea(5, 25);
			// textArea.setText("what even");
			// textArea.setEditable(false);
			// panel.add(new JScrollPane(textArea));

			// Add panel for target function address
			JPanel funcPanel = new JPanel(new BorderLayout());
			JTextField funcJtf = new JTextField();
			// addressJtf.setText("enter address");
			// addressJtf.setText("f_003");
			funcJtf.setEditable(true);
			funcPanel.add(funcJtf, BorderLayout.CENTER);
			funcPanel.add(new JLabel("Function"), BorderLayout.WEST);

			// Add panel for target function variable names
			JPanel varNamesPanel = new JPanel(new BorderLayout());
			JTextField varNamesJtf = new JTextField();
			// varNamesJtf.setText("enter comma separated variable names");
			// varNamesJtf.setText("a0,a1,a2");
			varNamesJtf.setEditable(true);
			varNamesPanel.add(varNamesJtf, BorderLayout.CENTER);
			varNamesPanel.add(new JLabel("Variable names"), BorderLayout.WEST);

			// Add panel for target function variable names
			JPanel varCtypesPanel = new JPanel(new BorderLayout());
			JTextField varCtypesJtf = new JTextField();
			// varCtypesJtf.setText("enter comma separated variable types");
			// varCtypesJtf.setText("int,int,int");
			varCtypesJtf.setEditable(true);
			varCtypesPanel.add(varCtypesJtf, BorderLayout.CENTER);
			varCtypesPanel.add(new JLabel("Variable types"), BorderLayout.WEST);
			JButton inferButton = new JButton("infer");
			varCtypesPanel.add(inferButton, BorderLayout.EAST);
			inferButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String funcId = funcJtf.getText();

					//Function func = getFunctionByName(funcName);
					//Function func = program.getFunctionManager().getFunctionAt(program.getAddressFactory().getDefaultAddressSpace().getAddress(Long.decode(funcName)));
					Function func = getFunctionByNameOrAddr(funcId);
					System.out.println("Called functions");
					for (Function f : func.getCalledFunctions(TaskMonitor.DUMMY)) {
						System.out.println(f.getName() + "@0x" + f.getEntryPoint());
					}

					// System.out.println("Return type: " + fs.getReturnType());
					// System.out.println(hf.getFunction().getPrototypeString(true, true));
					// DecompiledFunction df = dr.getDecompiledFunction();
					// System.out.println(df.getSignature());
					List<String> paramDataTypes = getParamDataTypeStrs(func);

					varCtypesJtf.setText(String.join(",", paramDataTypes));
					// String retType = retTypeJtf.getText();
				}
			});

			// Add panel for target function variable names
			JPanel retTypePanel = new JPanel(new BorderLayout());
			JTextField retTypeJtf = new JTextField();
			// retTypeJtf.setText("enter return type");
			// retTypeJtf.setText("float");
			retTypeJtf.setEditable(true);
			retTypePanel.add(retTypeJtf, BorderLayout.CENTER);
			retTypePanel.add(new JLabel("Return Type"), BorderLayout.WEST);

			JTextArea outputJta = new JTextArea(5, 25);
			outputJta.setLineWrap(true);
			//outputJta.setEditable(false);

			JPanel calledFuncsPanel = new JPanel();
			calledFuncsPanel.setLayout(new BoxLayout(calledFuncsPanel, BoxLayout.PAGE_AXIS));

			JButton calledFuncsButton = new JButton("Find Called Funcs");
			calledFuncsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					calledFuncsPanel.removeAll();
					String funcId = funcJtf.getText();
					Function func = getFunctionByNameOrAddr(funcId);

					// Get instructions:
					AddressSetView funcAddressSetView = func.getBody();
					System.out.println("Instructions");
					for (Instruction insn : program.getListing().getInstructions(funcAddressSetView, true)) {
						FlowType flowType = insn.getFlowType();
						Address addr = insn.getAddress();
						boolean isCall = flowType.isCall();
						if (isCall) {
							JPanel calledFuncPanel = new JPanel();
							calledFuncPanel.setLayout(new BoxLayout(calledFuncPanel, BoxLayout.LINE_AXIS));
							JCheckBox symexCheckBox = new JCheckBox("SymEx ");

							calledFuncPanel.add(symexCheckBox);
							JLabel calledFuncLabel = new JLabel();
							calledFuncPanel.add(calledFuncLabel);
							JTextField funcNameJtf = new JTextField();

							calledFuncPanel.add(funcNameJtf);
							JTextField argTypesJtf = new JTextField();
							calledFuncPanel.add(argTypesJtf);
							JTextField retTypeJtf = new JTextField();
							calledFuncPanel.add(retTypeJtf);
							calledFuncsPanel.add(calledFuncPanel);

							symexCheckBox.addItemListener(new ItemListener() {
								public void itemStateChanged(ItemEvent e) {
									// TODO disable the other components when unchecked
								}
							});

							Address[] flows = insn.getFlows();
							String flowFuncName = "?";
							String flowFuncAddr = "?";
							System.out.printf("insn: %s address: %s type: %s is call: %s\n", insn, addr, flowType, isCall);
							for (Address flowAddr : flows) {

								Function flowFunc = program.getListing().getFunctionAt(flowAddr);
								flowFuncName = flowFunc.getName();
								flowFuncAddr = "0x" + flowFunc.getEntryPoint();


								System.out.printf("flow addr: %s func name: %s\n", flowAddr, flowFunc.getName());
								List<String> paramDataTypes = getParamDataTypeStrs(flowFunc);
								argTypesJtf.setText(String.join(",", paramDataTypes));
							}
							if (!flowFuncName.contentEquals("?")) {
								funcNameJtf.setText(flowFuncName);
							}
							calledFuncLabel.setText("0x" + addr + " --> " + flowFuncName + "@" + flowFuncAddr);


						}

					}

					//Function func = getFunctionByName(funcName);
					//Function func = program.getFunctionManager().getFunctionAt(program.getAddressFactory().getDefaultAddressSpace().getAddress(Long.decode(funcName)));
					/*
					System.out.println("Called functions");
					for (Function f : func.getCalledFunctions(TaskMonitor.DUMMY)) {
						System.out.println(f.getName() + "@" + f.getEntryPoint());
						JPanel calledFuncPanel = new JPanel();
						calledFuncPanel.setLayout(new BoxLayout(calledFuncPanel, BoxLayout.LINE_AXIS));
						JCheckBox symexCheckBox = new JCheckBox("SymEx ");

						calledFuncPanel.add(symexCheckBox);
						JLabel calledFuncLabel = new JLabel(f.getName() + "@0x" + f.getEntryPoint());
						calledFuncPanel.add(calledFuncLabel);
						JTextField funcNameJtf = new JTextField();
						funcNameJtf.setText(f.getName());
						calledFuncPanel.add(funcNameJtf);
						JTextField argTypesJtf = new JTextField();
						List<String> paramDataTypes = getParamDataTypeStrs(f);
						argTypesJtf.setText(String.join(",", paramDataTypes));
						calledFuncPanel.add(argTypesJtf);
						JTextField retTypeJtf = new JTextField();
						calledFuncPanel.add(retTypeJtf);
						calledFuncsPanel.add(calledFuncPanel);

						symexCheckBox.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent e) {
								// TODO disable the other components when unchecked
							}
						});
					}
					*/
					calledFuncsPanel.revalidate();
					calledFuncsPanel.repaint();

				}
			});

			JButton runButton = new JButton("Run");
			JButton killButton = new JButton("Stop");

			runButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String binFilePath = program.getExecutablePath();
					String funcId = funcJtf.getText();

					//Function func = getFunctionByName(funcName);
					//Function func = program.getFunctionManager().getFunctionAt(program.getAddressFactory().getDefaultAddressSpace().getAddress(Long.decode(funcName)));
					Function func = getFunctionByNameOrAddr(funcId);



					long funcAddr = func.getEntryPoint().getOffset();
					String[] varNames = varNamesJtf.getText().split(",");
					String[] varCtypes = varCtypesJtf.getText().split(",");
					String retType = retTypeJtf.getText();

					FuncPrototype targetFuncPrototype = new FuncPrototype(funcAddr, funcId, varNames, varCtypes,
							retType);
					List<FuncPrototype> shortCircuitFuncPrototypes = new LinkedList<FuncPrototype>();
					for (Component component : calledFuncsPanel.getComponents()) {
						JPanel calledFuncPanel = (JPanel) component;

						JCheckBox symexCheckBox = (JCheckBox) calledFuncPanel.getComponent(0);
						JLabel calledFuncLabel = (JLabel) calledFuncPanel.getComponent(1);
						JTextField funcJtf = (JTextField) calledFuncPanel.getComponent(2);
						JTextField argTypesJtf = (JTextField) calledFuncPanel.getComponent(3);
						JTextField retTypeJtf = (JTextField) calledFuncPanel.getComponent(4);

						if (!symexCheckBox.isSelected()) {

							String calledFuncNameStr = calledFuncLabel.getText(); // addr --> name
							String scFuncName = funcJtf.getText();
							String[] scVarCtypes = argTypesJtf.getText().split(",");
							String scRetType = retTypeJtf.getText();

							String funcAddrStr = calledFuncNameStr.substring(0, calledFuncNameStr.indexOf(" -->"));
							long scFuncAddr = Long.decode(funcAddrStr);
							System.out.println(
									calledFuncNameStr + " " + scFuncName + " " + scVarCtypes + " " + scRetType);
							// When short-circuiting functions we don't have control over the var names,
							// hence the null
							FuncPrototype scFuncPrototype = new FuncPrototype(scFuncAddr, scFuncName, null, scVarCtypes,
									scRetType);
							shortCircuitFuncPrototypes.add(scFuncPrototype);
						}
					}

					InputArgs inputArgs = new InputArgs(binFilePath, targetFuncPrototype, shortCircuitFuncPrototypes);
					Thread angrRunnerThread = new Thread() {
						public void run() {
							outputJta.setText("Running symbolic execution...");
							outputJta.repaint();
							angrRunner = new AngrRunner(binFilePath, inputArgs);
							try {
								runButton.setEnabled(false);
								killButton.setEnabled(true);
								String output = angrRunner.run();
								outputJta.setText(output);
								runButton.setEnabled(true);
								killButton.setEnabled(false);
							} catch (PythonScriptException pse) {
								Msg.showInfo(getClass(), panel, "Python Script Error", pse.getMessage());
							}
						}
					};
					angrRunnerThread.start();
				}
			});
			killButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					angrRunner.stop();
					runButton.setEnabled(true);
					killButton.setEnabled(false);

				}
			});
			killButton.setEnabled(false);

			panel.add(funcPanel);
			panel.add(varCtypesPanel);
			panel.add(varNamesPanel);
			panel.add(retTypePanel);
			panel.add(calledFuncsButton);
			panel.add(new JScrollPane(calledFuncsPanel));
			panel.add(runButton, BorderLayout.CENTER);
			panel.add(killButton, BorderLayout.CENTER);
			panel.add(new JScrollPane(outputJta));

			setVisible(true);
		}

		private Function getFunctionByName(String name) {
			FunctionManager fm = program.getFunctionManager();
			for (Function f : fm.getFunctions(true)) {
				if (f.getName().equals(name)) {
					return f;
				}
			}
			return null;
		}

		private Function getFunctionByNameOrAddr(String nameOrAddr) {
			try {
				long addr = Long.decode(nameOrAddr);
				Function func = program.getFunctionManager().getFunctionAt(program.getAddressFactory().getDefaultAddressSpace().getAddress(addr));
				return func;
			} catch(NumberFormatException nfe) {
				Function func = getFunctionByName(nameOrAddr);
				return func;

			}
		}

		public List<String> getParamDataTypeStrs(Function func) {
			List<String> paramDataTypes = new LinkedList<String>();
			System.out.println("Getting params for func: " + func.getName());
			DecompileResults dr = decompInterface.decompileFunction(func,
					decompInterface.getOptions().getDefaultTimeout(), null);
			HighFunction hf = dr.getHighFunction();
			FunctionPrototype fp = hf.getFunctionPrototype();
			System.out.println("Function Prototype:");
			System.out.println(fp.getReturnStorage());

			for (int i = 0; i < fp.getNumParams(); i++) {
				HighSymbol hp = fp.getParam(i);
				VariableStorage storage = hp.getStorage();
				String baseRegisterName = storage.getRegister().getBaseRegister().getName();
				if (baseRegisterName.startsWith("YMM")) {
					paramDataTypes.add("float");
				} else if (baseRegisterName.startsWith("R")) {
					paramDataTypes.add("int");
				} else {
					throw new RuntimeException("Unknown base register: " + baseRegisterName);
				}
				System.out.println("Storage: " + storage);
				System.out.println("Base Register: " + baseRegisterName);
				System.out.println("Type flags: " + storage.getRegister().getTypeFlags());
				System.out.println("Group: " + storage.getRegister().getGroup());
			}
			System.out.println("Function signature:");
			FunctionSignature fs = func.getSignature();// XXX This only works after Analysis->One Shot->Decompiler
														// Parameter ID
			System.out.println(fs.getGenericCallingConvention());
			System.out.println(fs);

			System.out.println("Parameters:");
			for (ParameterDefinition pd : fs.getArguments()) {
				DataType dt = pd.getDataType();
				System.out.println(dt);
				//paramDataTypes.add(dt.toString());
			}

			return paramDataTypes;
		}

		// TODO: Customize actions
		private void createActions() {
			/*
			 * action = new DockingAction("My Action", getName()) {
			 *
			 * @Override public void actionPerformed(ActionContext context) {
			 *
			 * Msg.showInfo(getClass(), panel, "Custom Action", "Hello!"); } };
			 * action.setToolBarData(new ToolBarData(Icons.ADD_ICON, null));
			 * action.setEnabled(true); action.markHelpUnnecessary();
			 * dockingTool.addLocalAction(this, action);
			 */
		}

		@Override
		public JComponent getComponent() {
			return panel;
		}
	}

}
