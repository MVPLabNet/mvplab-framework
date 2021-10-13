package app.codegen;

import app.codegen.builder.AdminControllerCodeBuilder;
import app.codegen.builder.CreateRequestCodeBuilder;
import app.codegen.builder.DeleteRequestCodeBuilder;
import app.codegen.builder.I18nCodeBuilder;
import app.codegen.builder.QueryCodeBuilder;
import app.codegen.builder.ReactListCodeBuilder;
import app.codegen.builder.ReactUpdateCodeBuilder;
import app.codegen.builder.ReactViewCodeBuilder;
import app.codegen.builder.ResponseCodeBuilder;
import app.codegen.builder.ServiceCodeBuilder;
import app.codegen.builder.UpdateRequestCodeBuilder;
import app.codegen.builder.WebControllerCodeBuilder;
import app.codegen.builder.WebServiceAPICodeBuilder;
import app.codegen.builder.WebServiceIMPLCodeBuilder;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author chi
 */
public class CodeGen {
    private final EntityModel entityModel;
    private final List<CodeGenType> types;
    private final Map<String, EnumModel> enumMappings = Maps.newHashMap();
    private final Map<CodeGenType, List<AbstractCodeBuilder>> builders = Maps.newHashMap();
    private final VelocityEngine templateEngine;
    private final JavaParser javaParser = new JavaParser(new ParserConfiguration());

    public CodeGen(Path path, List<CodeGenType> types) {
        this.entityModel = parse(path);
        this.types = types;

        templateEngine = new VelocityEngine();
        templateEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        templateEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        builders.put(CodeGenType.SERVICE,
            Lists.newArrayList(new ServiceCodeBuilder(entityModel, templateEngine)));

        builders.put(CodeGenType.WEB,
            Lists.newArrayList(new WebControllerCodeBuilder(entityModel, templateEngine)));

        builders.put(CodeGenType.ADMIN,
            Lists.newArrayList(new AdminControllerCodeBuilder(entityModel, templateEngine)));

        builders.put(CodeGenType.API,
            Lists.newArrayList(new CreateRequestCodeBuilder(entityModel, templateEngine),
                new UpdateRequestCodeBuilder(entityModel, templateEngine),
                new ResponseCodeBuilder(entityModel, templateEngine),
                new DeleteRequestCodeBuilder(entityModel, templateEngine),
                new QueryCodeBuilder(entityModel, templateEngine),
                new WebServiceAPICodeBuilder(entityModel, templateEngine),
                new WebServiceIMPLCodeBuilder(entityModel, templateEngine)
            ));

        builders.put(CodeGenType.REACT,
            Lists.newArrayList(new ReactListCodeBuilder(entityModel, templateEngine),
                new ReactUpdateCodeBuilder(entityModel, templateEngine),
                new ReactViewCodeBuilder(entityModel, templateEngine)
            ));

        builders.put(CodeGenType.I18N,
            Lists.newArrayList(new I18nCodeBuilder(entityModel, templateEngine)));
    }

    public CodeGen setGroup(String groupName) {
        if (groupName.endsWith("/")) {
            this.entityModel.group = groupName;
        } else {
            this.entityModel.group = groupName + "/";
        }
        return this;
    }

    public EntityModel entityModel() {
        return entityModel;
    }

    public VelocityEngine templateEngine() {
        return templateEngine;
    }

    public void generate() {
        try {
            if (isTypeEnabled(CodeGenType.SERVICE)) {
                generate(CodeGenType.SERVICE);
            }
            if (isTypeEnabled(CodeGenType.API)) {
                generate(CodeGenType.API);
            }
            if (isTypeEnabled(CodeGenType.ADMIN)) {
                generate(CodeGenType.ADMIN);
            }
            if (isTypeEnabled(CodeGenType.WEB)) {
                generate(CodeGenType.WEB);
            }
            if (isTypeEnabled(CodeGenType.REACT)) {
                generate(CodeGenType.REACT);
            }
            if (isTypeEnabled(CodeGenType.I18N)) {
                generate(CodeGenType.I18N);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generate(CodeGenType type) throws IOException {
        List<AbstractCodeBuilder> codeBuilders = builders.get(type);
        for (AbstractCodeBuilder codeBuilder : codeBuilders) {
            Path path = codeBuilder.path();
            String code = codeBuilder.build();
            System.out.println("Generate " + path);
            Files.createDirectories(path.getParent());
            Files.write(path, code.getBytes(Charsets.UTF_8));
        }
    }

    private EntityModel parse(Path path) {
        try {
            if (!path.toFile().exists()) {
                throw new RuntimeException("missing file, path=" + path);
            }

            CompilationUnit compilationUnit = javaParser.parse(Files.readString(path)).getResult().orElseThrow();

            Map<String, String> imports = Maps.newHashMap();
            for (ImportDeclaration anImport : compilationUnit.getImports()) {
                Name value = (Name) anImport.getChildNodes().get(0);
                String fullName = value.asString();
                int index = fullName.lastIndexOf(".");
                if (index > 0) {
                    imports.put(fullName.substring(index + 1), fullName.substring(0, index));
                }
            }
            EntityModel entityModel = new EntityModel();
            entityModel.fileName = path.getFileName().toString();
            entityModel.type = compilationUnit.getTypes().get(0).getName().toString();
            entityModel.packageName = compilationUnit.getPackageDeclaration().get().getName().toString();
            entityModel.shortName = shortName(entityModel.type);
            entityModel.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityModel.type);
            int index = entityModel.packageName.indexOf(".domain");
            if (index > 0) {
                entityModel.basePackage = entityModel.packageName.substring(0, index);
                entityModel.basePath = path.getParent().getParent();
            } else {
                entityModel.basePackage = entityModel.packageName;
                entityModel.basePath = path.getParent();
            }
            entityModel.path = path;
            entityModel.members = Lists.newArrayList();

            Path rootPath = rootPath(path, entityModel.packageName);
            VoidVisitor<Void> visitor = new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(FieldDeclaration n, Void arg) {
                    MemberModel memberModel = new MemberModel();
                    Optional<AnnotationExpr> idAnnotation = n.getAnnotationByName("Id");
                    memberModel.type = n.getCommonType().toString();
                    memberModel.name = n.getVariable(0).getNameAsString();
                    memberModel.packageName = imports.get(memberModel.type);
                    if (memberModel.packageName == null && isSamePackage(entityModel.path.getParent(), memberModel.type)) {
                        memberModel.packageName = entityModel.packageName;
                    }
                    memberModel.isId = idAnnotation.isPresent();
                    memberModel.isIdGenerated = n.getAnnotationByName("GeneratedValue").isPresent();
                    Optional<EnumModel> enumModel = parseEnum(rootPath, memberModel);
                    if (enumModel.isPresent()) {
                        memberModel.isEnum = true;
                        enumMappings.put(enumModel.get().type, enumModel.get());
                    } else {
                        memberModel.isEnum = false;
                    }
                    memberModel.inputType = inputType(memberModel.type, memberModel.length);

                    Optional<AnnotationExpr> columnOptional = n.getAnnotationByName("Column");
                    if (columnOptional.isPresent()) {
                        AnnotationExpr column = columnOptional.get();
                        for (Node childNode : column.getChildNodes()) {
                            if (childNode instanceof MemberValuePair) {
                                MemberValuePair valuePair = (MemberValuePair) childNode;
                                if (valuePair.getName().asString().equals("length")) {
                                    memberModel.length = Integer.parseInt(valuePair.getValue().toString());
                                    break;
                                }
                            }
                        }
                    }
                    Set<String> excludes = Sets.newHashSet("Column", "Id", "GeneratedValue", "Enumerated");
                    List<AnnotationModel> annotationModels = Lists.newArrayList();
                    for (AnnotationExpr annotation : n.getAnnotations()) {
                        if (excludes.contains(annotation.getName().toString())) {
                            continue;
                        }
                        AnnotationModel annotationModel = new AnnotationModel();
                        annotationModel.type = annotation.getName().toString();
                        annotationModel.definition = annotation.toString();
                        annotationModel.packageName = imports.get(annotationModel.type);
                        if (annotationModel.packageName == null && isSamePackage(entityModel.path.getParent(), annotationModel.type)) {
                            annotationModel.packageName = entityModel.packageName;
                        }
                        annotationModel.isConstraints = annotationModel.packageName.contains(".constraints");
                        annotationModel.values = Maps.newHashMap();
                        for (Node childNode : annotation.getChildNodes()) {
                            if (childNode instanceof MemberValuePair) {
                                MemberValuePair valuePair = (MemberValuePair) childNode;
                                annotationModel.values.put(valuePair.getName().toString(), valuePair.getValue().toString());
                            }
                        }
                        annotationModels.add(annotationModel);
                    }
                    memberModel.annotations = annotationModels;
                    if (memberModel.isId) {
                        entityModel.id = memberModel;
                    } else {
                        entityModel.members.add(memberModel);
                    }
                }
            };
            compilationUnit.accept(visitor, null);
            entityModel.enumTypes = Lists.newArrayList(enumMappings.values());
            return entityModel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Optional<EnumModel> parseEnum(Path rootPath, MemberModel member) {
        try {
            if (member.packageName == null) {
                return Optional.empty();
            }
            if (enumMappings.containsKey(member.type)) {
                return Optional.of(enumMappings.get(member.type));
            }
            Path path = rootPath.resolve(member.packageName.replaceAll("\\.", "/")).resolve(member.type + ".java");
            List<String> values = Lists.newArrayList();
            String enumType = null;
            if (path.toFile().exists()) {
                CompilationUnit compilationUnit = javaParser.parse(Files.readString(path)).getResult().orElseThrow();
                TypeDeclaration<?> target = compilationUnit.getType(0);
                enumType = target.getName().toString();
                for (Node childNode : target.getChildNodes()) {
                    if (childNode instanceof EnumConstantDeclaration) {
                        EnumConstantDeclaration enumValue = (EnumConstantDeclaration) childNode;
                        values.add(enumValue.getNameAsString());
                    }
                }
            }

            if (values.isEmpty()) {
                return Optional.empty();
            }

            EnumModel enumModel = new EnumModel();
            enumModel.name = member.name;
            enumModel.type = enumType;
            enumModel.values = values;
            return Optional.of(enumModel);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String inputType(String type, Integer length) {
        switch (type) {
            case "String":
                if (length != null && length >= 512) {
                    return "textarea";
                } else {
                    return "text";
                }
            case "Integer":
            case "Long":
            case "Double":
            case "BigDecimal":
            case "Float":
                return "number";
            case "Date":
            case "LocalDateTime":
            case "OffsetDateTime":
            case "ZonedDateTime":
                return "datepicker";
            default:
                return "text";
        }
    }

    private String shortName(String typeName) {
        char[] chars = typeName.toCharArray();
        boolean found = false;
        int i = chars.length - 1;
        for (; i >= 0; i--) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                found = true;
            } else {
                if (found) {
                    break;
                }
            }
        }
        return typeName.substring(Math.max(i + 1, 0)).toLowerCase();
    }

    private boolean isSamePackage(Path basePath, String typeName) {
        return basePath.resolve(typeName + ".java").toFile().exists();
    }

    Path rootPath(Path path, String packageName) {
        Path current = path.getParent();
        Iterator<String> iterator = Splitter.on(".").splitToList(packageName).iterator();
        while (iterator.hasNext() && current != null) {
            iterator.next();
            current = current.getParent();
        }
        return current;
    }

    private boolean isTypeEnabled(CodeGenType type) {
        return types.contains(type);
    }

    public enum CodeGenType {
        SERVICE, API, IMPL, ADMIN, WEB, REACT, I18N
    }


}
