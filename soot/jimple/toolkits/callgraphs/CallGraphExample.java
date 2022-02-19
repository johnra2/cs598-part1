package soot.jimple.toolkits.callgraph;

import com.fasterxml.jackson.dataformat.csv.CsvParser;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.*;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.dataformat.toml.TomlFactory;
//import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class CallGraphExample
{

    public static void main(String[] args) {

        String targetPath = "/toml/target/classes"; // or "/csv/target/classes" or "/yaml/target/classes" etc
        String outputPath = "TomlFactoryCallGraph.dot"; // or "YamlCallGraph.dot" etc.

        List<String> csvParserMethods = new ArrayList<String>();
        for (Method method : TomlFactory.class.getDeclaredMethods()) { // Update [Class].class to the "Main" class
            String name = method.getName();
            csvParserMethods.add(name);
        }

        // Soot classpath
        String classpath = System.getProperty("user.dir") + targetPath;

        // Setting the classpath programatically
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(classpath); //path
        Options.v().set_allow_phantom_refs(true);

        args = new String[]{"-w", "-process-dir", classpath};
        Main.main(args);

        CallGraph cg = Scene.v().getCallGraph();

        try {
            File myObj = new File(outputPath); // Update to output name
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
                if (csvParserMethods.contains(srcName) || csvParserMethods.contains(tgtName)) {
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

            System.out.println("Dot Iter Size: " + dotEdges.size());

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



        System.out.println("DONE");

    }
}
