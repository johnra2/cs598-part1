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

public class CallGraphGenerator
{

    public static void main(String[] args) {

        // Program takes in three arguments.
        // 1) The Target path which contains the built class files
        // 2) The output path to generate the Call Graph in
        // 3) The repo under test: either CSV, TOML, or YAML.
        String targetPath = args[0];            // something like "/toml/target/classes"
        String outputPath = args[1];            // something like "TomlFactoryCallGraph.dot"
        String repo = args[2].toUpperCase();    // something like "TOML"

        // Get the list of relevant methods for the class under test based on its repo
        List<String> methodList = new ArrayList<String>();
        switch (repo) {
            case "CSV":
                for (Method method : CsvParser.class.getDeclaredMethods()) {
                    String name = method.getName();
                    methodList.add(name);
                }
                break;
            case "TOML":
                for (Method method : TomlFactory.class.getDeclaredMethods()) {
                    String name = method.getName();
                    methodList.add(name);
                }
                break;
            case "YAML":
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

        args = new String[]{"-w", "-process-dir", classpath};
        Main.main(args);

        // Generate Call Graph
        CallGraph cg = Scene.v().getCallGraph();

        try {
            File myObj = new File(outputPath);
            myObj.createNewFile();

            // Set of edges in dot format
            Set<String> dotEdges = new HashSet<String>();

            Set<Edge> edges = cg.edges;
            Iterator<Edge> iter = edges.iterator();

            // Iterate through all the edges in cg.edges and add them to our dotEdges set
            while(iter.hasNext()) {
                Edge edge = iter.next();
                SootMethod src = edge.src();
                SootMethod tgt = edge.tgt();

                String srcName = src.getName();
                String tgtName = tgt.getName();

                // Only if the methods are related to the class under test
                if (methodList.contains(srcName) || methodList.contains(tgtName)) {
                    if (!srcName.contains("$") && !tgtName.contains("$")) {
                        dotEdges.add("\t" + srcName + " -> " + tgtName + ";\n");
                    }
                }

            }

            // Actually write the contents to a dot file
            FileWriter log = new FileWriter(myObj);
            log.write("digraph G {\n");
            Iterator<String> dotIter = dotEdges.iterator();

            while(dotIter.hasNext()) {
                log.write(dotIter.next());
            }

            log.write("}");
            log.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
