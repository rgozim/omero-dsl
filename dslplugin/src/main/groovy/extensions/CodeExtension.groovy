package extensions

class CodeExtension extends OperationExtension {

    File outputPath

    Closure formatOutput

    CodeExtension(String name) {
        super(name)
    }

    void setOutputPath(String dir) {
        this.outputPath = new File(dir)
    }

    void outputPath(String dir) {
        setOutputPath(dir)
    }

    void outputPath(File dir) {
        this.outputPath = dir
    }

    void formatOutput(Closure closure) {
        this.formatOutput = closure
    }
}