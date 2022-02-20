package soot.jimple.toolkits.callgraph;

import polyglot.ast.Node;
import polyglot.frontend.SourceJob;
import polyglot.visit.NodeVisitor;
import soot.*;
import soot.javaToJimple.ppa.PPAEngine;
import soot.javaToJimple.ppa.jj.PPAExtensionInfo;
import soot.options.Options;

import java.lang.reflect.Method;
import java.util.*;

// Import the classes under test
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class AbstractSyntaxTree
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
        Options.v().set_output_dir(outputPath);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_src_prec(Options.src_prec_java);

        // Load the class under test
        Options.v().classes().add(className);
        Scene.v().loadNecessaryClasses();

        // Construct the AST using PPA Polyglot
        PPAExtensionInfo ppaInfo = PPAEngine.v().getExtensionInfo();
        for (Object oJob : ppaInfo.sourceJobMap().values()) {
            SourceJob sJob = (SourceJob) oJob;
            Node node = sJob.ast();
            PrintVisitor visitor = new PrintVisitor();
            node.visit(visitor);
        }
    }
}

// Visitor to walk through the AST and print out the nodes
class PrintVisitor extends NodeVisitor {

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        System.out.println(n.getClass().toString());
        return super.leave(old, n, v);
    }

}
