package hr.algebra.threerp3.stratego.utils;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentationUtils {
    public static void generateDocumentation() {

        Path targetPath = Path.of("target");

        try (Stream<Path> paths = Files.walk(targetPath)) {
            List<String> classFiles = paths
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".class"))
                    .filter(file -> !file.endsWith("module-info.class"))
                    .toList();

            StringBuilder headerHtml = new StringBuilder();

            headerHtml.append("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                    <title>Page Title</title>
                    </head>
                    <body>
                                    
                    """);

            for (String classFile : classFiles) {
                String[] classFileTokens = classFile.split("classes");
                String classFilePath = classFileTokens[1];
                String reducedClassFilePath =
                        classFilePath.substring(1, classFilePath.lastIndexOf('.'));
                String fullyQualifiedName = reducedClassFilePath.replace('\\', '.');

                headerHtml.append("<h2>" + fullyQualifiedName + "</h2>");

                Class<?> deserializedClass = Class.forName(fullyQualifiedName);

                readClassAndMembersInfo(deserializedClass, headerHtml);
            }

            String footerHtml = """
                    </body>
                    </html>
                    """;

            Path documentationFilePath = Path.of("files/documentation.html");

            String fullHtml = headerHtml + footerHtml;

            Files.write(documentationFilePath, fullHtml.getBytes());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void readClassInfo(Class<?> clazz, StringBuilder classInfo) {
        appendPackage(clazz, classInfo);
        classInfo.append("<br>");
        classInfo.append("<br>");
        appendModifiers(clazz, classInfo);
        classInfo
                .append(" ")
                .append(clazz.getSimpleName());
        appendParent(clazz, classInfo, true);
        appendInterfaces(clazz, classInfo);
    }

    public static void appendPackage(Class<?> clazz, StringBuilder classInfo) {
        classInfo
                .append(clazz.getPackage());
    }

    public static void appendModifiers(Class<?> clazz, StringBuilder classInfo) {
        classInfo.append(Modifier.toString(clazz.getModifiers()));
    }

    public static void appendParent(Class<?> clazz, StringBuilder classInfo, boolean first) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return;
        }
        classInfo
                .append(first ? " extends " : " -> ")
                .append(parent.getSimpleName());
        appendParent(parent, classInfo, false);
    }

    public static void appendInterfaces(Class<?> clazz, StringBuilder classInfo) {
        if (clazz.getInterfaces().length > 0) {
            classInfo
                    .append(" implements ")
                    .append(Stream.of(clazz.getInterfaces())
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", ")));
        }
    }

    public static void readClassAndMembersInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        readClassInfo(clazz, classAndMembersInfo);
        classAndMembersInfo.append("<br>");
        classAndMembersInfo.append("<br>");
        classAndMembersInfo.append("FIELDS: ");
        appendFields(clazz, classAndMembersInfo);
        classAndMembersInfo.append("<br>");
        classAndMembersInfo.append("<br>");
        classAndMembersInfo.append("METHODS: ");
        appendMethods(clazz, classAndMembersInfo);
        classAndMembersInfo.append("<br>");
        classAndMembersInfo.append("<br>");
        classAndMembersInfo.append("CONSTRUCTORS: ");
        appendConstructors(clazz, classAndMembersInfo);
    }

    private static void appendFields(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Field[] fields = clazz.getDeclaredFields();
        classAndMembersInfo
                .append(Stream.of(fields)
                        .map(field -> cutPackageName(Objects.toString(field)))
                        .collect(Collectors.joining("<br>")));
    }

    private static String cutPackageName(String input) {
        String transformedString = input.replaceAll("\\b(?:\\w+\\.)+", "");

        transformedString = transformedString.replaceAll("\\b(?!lambda\\$)(\\w+)\\.(\\w+)\\b", "$2");

        return transformedString.trim();
    }

    private static void appendMethods(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Method[] methods = clazz.getDeclaredMethods();
        //iter tab tab
        for (Method method : methods) {
            classAndMembersInfo
                    .append("<br>");
            appendAnnotations(method, classAndMembersInfo);
            classAndMembersInfo
                    .append(Modifier.toString(method.getModifiers()))
                    .append(" ")
                    .append(method.getReturnType())
                    .append(" ")
                    .append(cutPackageName(method.getName()));
            appendParameters(method, classAndMembersInfo);
            appendExceptions(method, classAndMembersInfo);
        }
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            classAndMembersInfo
                    .append("<br>");
            appendAnnotations(constructor, classAndMembersInfo);
            classAndMembersInfo
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(cutPackageName(constructor.getName()));
            appendParameters(constructor, classAndMembersInfo);
            appendExceptions(constructor, classAndMembersInfo);
        }
    }


    private static void appendAnnotations(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append(
                Stream.of(executable.getAnnotations())
                        .map(Objects::toString)
                        .collect(Collectors.joining("<br>")));
    }

    private static void appendParameters(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append(
                Stream.of(executable.getParameters())
                        .map(Objects::toString)
                        .collect(Collectors.joining(", ", "(", ")"))
        );
    }

    private static void appendExceptions(Executable executable, StringBuilder classAndMembersInfo) {
        if (executable.getExceptionTypes().length > 0) {
            classAndMembersInfo.append(
                    Stream.of(executable.getExceptionTypes())
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", ", " throws ", ""))); // no String.empty like in C#
        }
    }

}