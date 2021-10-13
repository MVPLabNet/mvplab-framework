package app.codegen.builder;

import app.codegen.CodeGen;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * @author chi
 */
class AdminControllerCodeBuilderTest {
    @Test
    void build() {
        CodeGen codeGen = new CodeGen(Paths.get("D:\\Workspace\\gitlab.com\\jweb-codegen-project\\codegen\\src\\test\\java\\app\\jweb\\test\\domain\\Patient.java"), Lists.newArrayList());
        AdminControllerCodeBuilder codeBuilder = new AdminControllerCodeBuilder(codeGen.entityModel(), codeGen.templateEngine());
        String code = codeBuilder.build();
        assertNotNull(code);
        System.out.println(code);
    }
}