package app.codegen;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author chi
 */
class CodeGenTest {
    @Test
    void generate() {
        String entityPath = "D:\\Workspace\\gitlab.com\\patient-project\\website\\src\\main\\java\\app\\patient\\domain\\Pharmacy.java";
        CodeGen codeGen = new CodeGen(Paths.get(entityPath), Arrays.asList(CodeGen.CodeGenType.I18N));
        codeGen.generate();
    }

    @Test
    void isEnum() throws Exception {
        String entityPath = "D:\\Workspace\\gitlab.com\\patient-project\\website\\src\\main\\java\\app\\patient\\domain\\Pharmacy.java";
        CodeGen codeGen = new CodeGen(Paths.get(entityPath), Arrays.asList(CodeGen.CodeGenType.SERVICE, CodeGen.CodeGenType.API, CodeGen.CodeGenType.IMPL,
            CodeGen.CodeGenType.ADMIN, CodeGen.CodeGenType.WEB, CodeGen.CodeGenType.REACT));

        MemberModel memberModel = new MemberModel();
        memberModel.packageName = "app.test.domain";
        memberModel.type = "GenderType";
        memberModel.name = "gender";

        Optional<EnumModel> enumModel = codeGen.parseEnum(Paths.get("D:\\Workspace\\gitlab.com\\jweb-codegen-project\\codegen\\src\\test\\java"), memberModel);
        assertTrue(enumModel.isPresent());
    }
}