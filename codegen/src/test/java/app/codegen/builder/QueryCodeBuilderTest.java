package app.codegen.builder;

import app.codegen.CodeGen;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

/**
 * @author chi
 */
class QueryCodeBuilderTest {
    @Test
    void build() {
        CodeGen codeGen = new CodeGen(Paths.get("D:\\Workspace\\gitlab.com\\jweb-codegen-project\\codegen\\src\\test\\java\\app\\jweb\\test\\domain\\Patient.java"), Lists.newArrayList());
        QueryCodeBuilder codeBuilder = new QueryCodeBuilder(codeGen.entityModel(), codeGen.templateEngine());
        String code = codeBuilder.build();
        System.out.println(code);
    }
}