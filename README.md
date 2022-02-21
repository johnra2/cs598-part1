# Milestone 1

Our team went about using Soot to provide Static Analysis on 3 jackson-dataformats-text repos: jackson-dataformats-csv, jackson-dataformats-yaml, jackson-dataformats-toml. The jackson-dataformats-csv repo is used for reading and writing CSV data in the Jackson data format module. The jackson-dataformats-yaml is ised for reading and writing YAML encoded data. The jackson-dataformat toml repo is used fro reading and writing TOML files for the Jackson data format module. 

## Process

### Identifying, Compiling, and Testing Repos
In ordinance with the initial steps, our team found 10 repositiories that had over 50 stars and over 50 tests. 4 repos were directories from the PMD repo, 3 were directories from the jackson-dataformat-text repo, and the rest were commons repos from apache. These repos were compiled and tested, we spoke with the professor in OH and she told us no screenshots of this were needed.

With the new guidelines, were decided to further analyze the three jackson-dataformat repos, so you will find their graphs in their respective repos.

### Graph generating steps

To generate the images for our graphs, we made three different scripts available in the `src` folder. For all three scripts, they are called in the terminal using the format `java [file] [target path] [output path] [repository]`.
For example, calling `java AbstractSyntaxTreeGenerator.java /toml/target/classes TomlFactoryCallGraph.dot TOML` would generate an AST data file for Toml Factory under the `jackson-dataformats` library. It would use the `.class` files in the
`/toml/target/classes` folder and create an output file called `TomlFactoryCallGraph.dot`.

After generating the `.dot` file, we used GraphViz to transform the data into into a `.svc` file for visual use.

NOTE: This methodolgy only works for `CallGraphGenerator.java`. Abstract Syntax Trees and Control Flow Graphs are generated using alternative methods, we just wanted to link how they would have been done if Soot had worked properly for them in their respective java files.

#### Call Graphs
The Call graphs were generated using Soot and all the code for this is in `cs598-part1/src/CallGraphGenerator.java`

1. The arguments from the above command are captured as the `targetPath`, `outputPath`, and destination `repo`

```java
// Program takes in three arguments.
// 1) The Target path which contains the built class files
// 2) The output path to generate the Call Graph in
// 3) The repo under test: either CSV, TOML, or YAML.
String targetPath = args[0];            // something like "/toml/target/classes"
String outputPath = args[1];            // something like "TomlFactoryCallGraph.dot"
String repo = args[2].toUpperCase();    // something like "TOML"
```

2. All the relevant method names are retrieved and put into an arraylist based on what the value of `repo` is: `"CSV"` maps to the `CsvParser` class, `"TOML"` maps to the `TomlFactory` class, `"YAML"` maps to the `YAMLGenerator` class.

```java
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
```
3. The Soot classpath was created and set programmically, with its `args` set to `Main`

```java
// Soot classpath
String classpath = System.getProperty("user.dir") + targetPath;

// Setting the classpath programatically
Options.v().set_prepend_classpath(true);
Options.v().set_soot_classpath(classpath);
Options.v().set_allow_phantom_refs(true);

args = new String[]{"-w", "-process-dir", classpath};
Main.main(args);
```

4. The Call Graph is generated, traversed, and the dotEdges are written to the dot file as the output. If there is an `IOException`, the stacktrace will be printed

```java
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
```

The Call graphs, dot file and svg image file, can be found in the respective repos under `CG`

#### Abstact Syntax Trees
Because the AST was not able to be generated using Soot, our team decided to use the JavaParser AST Inspector plugin in IntelliJ Idea. The steps for how they are producted are below:


The file is exported to a `Custom Dot` File where the AST can be viewed in the respective repo under `AST`.

#### Control-Flow Graphs
Because the Control-Flow Graph was not able to be generated using Soot, our team decided to use the online public website [code2flow](https://code2flow.com/). The steps to create a Control-Flow Graph are below:

1. Create a Draft after creating an account
![image info](.\markdown_images\CFG_step1.JPG)

2. Delete the Code in the left terminal to have an empty flowchart
![image info](.\markdown_images\CFG_step2a.JPG)
![image info](.\markdown_images\CFG_step2b.JPG)

3. Paste the method from the desired class file you want to create the Control-Flow Graph for. For this example we will use the `writeEndArray` method in the `YAMLGenerator` class. 

NOTE: These class files are the same files from step 2 of the Call Graph section.

![image info](.\markdown_images\CFG_step3.JPG)

4. Hit the Share button, then the Download tab, and then download the file as a SVG.

![image info](.\markdown_images\CFG_step4a.JPG)
![image info](.\markdown_images\CFG_step4b.JPG)

5. Repeat the above steps to create more Control-Flow Graphs on methods for the desired class

NOTE: The files will need to be put in the subdirectory `CFG` based on which class the process is done for: the `CsvParser` class for jackson-dataformat-csv repo, the `TomlFactory` class for the jackson-dataformat-toml repo, and the `YAMLGenerator` class for the jackson-dataformat-yaml.


## Future Work
If given more time, our team would have loved to have gotten a better understanding of how Soot worked in order to make the AST and Control-Flow Graph Java files work properly.