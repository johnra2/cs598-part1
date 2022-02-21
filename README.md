# cs598-part1

# Graph generating steps

To generate the images for our graphs, we made three different scripts available in the `src` folder. For all three scripts, they are called in the terminal using the format `java [file] [target path] [output path] [repository]`.
For example, calling `java AbstractSyntaxTreeGenerator.java /toml/target/classes TomlFactoryCallGraph.dot TOML` would generate an AST data file for Toml Factory under the `jackson-dataformats` library. It would use the `.class` files in the
`/toml/target/classes` folder and create an output file called `TomlFactoryCallGraph.dot`.

After generating the `.dot` file, we used GraphViz to transform the data into into a `.svc` file for visual use.