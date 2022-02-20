package soot.jimple.toolkits.callgraph;

import soot.*;
import soot.options.Options;

import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.*;

import java.io.File;
import java.io.IOException;

// Import the classes under test
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import soot.toolkits.graph.BlockGraphConverter;
import soot.toolkits.graph.CompleteBlockGraph;

public class ControlFlowGraphGenerator
{

    public static void main(String[] args) {

        // Program takes in three arguments.
        // 1) The Target path which contains the built class files
        // 2) The output path to generate the Call Graph in
        // 3) The repo under test: either CSV, TOML, or YAML.
        String targetPath = args[0];            // something like "/toml/target/classes"
        String outputPath = args[1];            // something like "TomlFactoryCallGraph.dot"
        String repo = args[2].toUpperCase();    // something like "TOML"
        String className = "";

        // Get the list of relevant methods for the class under test based on its repo
        List<String> methodList = new ArrayList<String>();
        switch (repo) {
            case "CSV":
                className = "com.fasterxml.jackson.dataformat.csv.CsvParser";
                for (Method method : CsvParser.class.getDeclaredMethods()) {
                    String name = method.getName();
                    methodList.add(name);
                }
                break;
            case "TOML":
                className = "com.fasterxml.jackson.dataformat.toml.TomlFactory";
                for (Method method : TomlFactory.class.getDeclaredMethods()) {
                    String name = method.getName();
                    methodList.add(name);
                }
                break;
            case "YAML":
                className = "com.fasterxml.jackson.dataformat.yaml.YAMLGeneraor";
                for (Method method : YAMLGenerator.class.getDeclaredMethods()) {
                    String name = method.getName();
                    methodList.add(name);
                }
                break;
            default:
                System.out.println("Please insert a valid repo: CSV, TOML, YAML");
                return;
        }

        // Soot classpath
        String classpath = System.getProperty("user.dir") + targetPath;

        // Setting the classpath programatically
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(classpath);
        Options.v().set_allow_phantom_refs(true);

        // Load the Class and its methods
        Scene.v().loadClassAndSupport(className);
        Scene.v().loadNecessaryClasses();
        SootClass sc = Scene.v().getSootClass(className);

        // Get the CFG for each method in the class
        for (String methodName : methodList) {
            SootMethod sm = sc.getMethodByName(methodName);
            Body b = sm.retrieveActiveBody();
            CompleteBlockGraph cfg = new CompleteBlockGraph(b);
            BlockGraphConverter.addStartStopNodesTo(cfg);
            BlockGraphConverter.reverse(cfg);

            // Write the CFG to a file
            try {
                File myObj = new File(methodName + ".dot");
                myObj.createNewFile();
                FileWriter log = new FileWriter(myObj);
                log.write(cfg.toString());
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }

    }
}
