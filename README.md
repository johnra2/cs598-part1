# Milestone 1

Our team went about using Soot to provide Static Analysis on 3 jackson-dataformats-text repos: jackson-dataformats-csv, jackson-dataformats-yaml, jackson-dataformats-toml. The jackson-dataformats-csv repo is used for reading and writing CSV data in the Jackson data format module. The jackson-dataformats-yaml is ised for reading and writing YAML encoded data. The jackson-dataformat toml repo is used fro reading and writing TOML files for the Jackson data format module. 

## Process

### Identifying Repos

### Compiling and Testing Repos

### Call Graphs

### Abstact Syntax Trees

JavaParser AST Inspector -> Export to Dot File

csv/CsvParser

toml/TomlFactory

yaml/YamlGenerator

# Graph generating steps

To generate the images for our graphs, we made three different scripts available in the `src` folder. For all three scripts, they are called in the terminal using the format `java [file] [target path] [output path] [repository]`.
For example, calling `java AbstractSyntaxTreeGenerator.java /toml/target/classes TomlFactoryCallGraph.dot TOML` would generate an AST data file for Toml Factory under the `jackson-dataformats` library. It would use the `.class` files in the
`/toml/target/classes` folder and create an output file called `TomlFactoryCallGraph.dot`.

After generating the `.dot` file, we used GraphViz to transform the data into into a `.svc` file for visual use.

