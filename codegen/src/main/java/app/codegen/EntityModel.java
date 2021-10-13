package app.codegen;

import java.nio.file.Path;
import java.util.List;

public class EntityModel {
    public Path path;
    public Path basePath;
    public String group = "";
    public String basePackage;
    public String fileName;
    public String type;
    public String name;
    public String shortName;
    public String packageName;
    public List<EnumModel> enumTypes;

    public MemberModel id;
    public List<MemberModel> members;

    public String getGroup() {
        return group;
    }

    public List<EnumModel> getEnumTypes() {
        return enumTypes;
    }

    public String getName() {
        return name;
    }

    public MemberModel getId() {
        return id;
    }

    public Path getPath() {
        return path;
    }

    public Path getBasePath() {
        return basePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getType() {
        return type;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getShortName() {
        return shortName;
    }

    public List<MemberModel> getMembers() {
        return members;
    }

}

