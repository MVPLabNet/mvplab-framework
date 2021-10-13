import app.codegen.CodeGen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author chi
 */
public class Main {
    private static final String USAGE = "codegen [service,api,impl,admin,web,react] path-to-entity";

    public static void main(String[] args) {
        List<CodeGen.CodeGenType> types = Arrays.asList(CodeGen.CodeGenType.SERVICE, CodeGen.CodeGenType.API, CodeGen.CodeGenType.IMPL,
            CodeGen.CodeGenType.ADMIN, CodeGen.CodeGenType.WEB, CodeGen.CodeGenType.REACT, CodeGen.CodeGenType.I18N);
        String entityPath = "D:\\Workspace\\calendar-project\\calendar-backend\\calendar-service\\src\\main\\java\\app\\calendar\\domain\\SoftwareDistribution.java";

        File entityFile;
        if (entityPath.startsWith(".")) {
            Path dir = Paths.get(System.getProperty("user.dir"));
            entityFile = dir.resolve(entityPath).toFile();
        } else {
            entityFile = new File(entityPath);
        }

        if (entityFile.isDirectory()) {
            for (File file : entityFile.listFiles()) {
                if (file.getName().endsWith(".java")) {
                    CodeGen codeGen = new CodeGen(file.toPath(), types);
                    codeGen.generate();
                }
            }
        } else {
            CodeGen codeGen = new CodeGen(entityFile.toPath(), types);
            codeGen.generate();
        }
    }
}
