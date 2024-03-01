package projekt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.opentest4j.AssertionFailedError;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class SanityCheck {

    private static final List<String> PACKAGE_NAMES = List.of(
        "projekt",
        "projekt.controller",
        "projekt.controller.actions",
        "projekt.controller.gui",
        "projekt.controller.gui.tiles",
        "projekt.model",
        "projekt.model.buildings",
        "projekt.model.tiles",
        "projekt.view",
        "projekt.view.gameControls",
        "projekt.view.menus",
        "projekt.view.tiles"
    );
    private static final Map<String, Integer> MODIFIERS = Map.ofEntries(
        Map.entry("PUBLIC",       0x001),
        Map.entry("PRIVATE",      0x002),
        Map.entry("PROTECTED",    0x004),
        Map.entry("STATIC",       0x008),
        Map.entry("FINAL",        0x010),
        Map.entry("SYNCHRONIZED", 0x020),
        Map.entry("VOLATILE",     0x040),
        Map.entry("TRANSIENT",    0x080),
        Map.entry("NATIVE",       0x100),
        Map.entry("INTERFACE",    0x200),
        Map.entry("ABSTRACT",     0x400),
        Map.entry("STRICT",       0x800)
    );

    @ParameterizedTest
    @JsonParameterSetTest("/structure.json")
    public void test(JsonParameterSet jsonParameterSet) throws Throwable {
        ClassRecord classRecord = ClassRecord.deserialize(jsonParameterSet.getRootNode());
        Class<?> clazz;
        try {
            clazz = Class.forName(classRecord.identifier);
        } catch (ClassNotFoundException e) {
            throw new AssertionFailedError("Could not find class " + classRecord.identifier);
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            System.err.println("Could not load class " + classRecord.identifier);
            e.printStackTrace();
            return;
        }

        {
            int expectedModifiers = classRecord.modifiers
                .stream()
                .mapToInt(MODIFIERS::get)
                .sum();
            assertEquals(expectedModifiers, clazz.getModifiers() & expectedModifiers, emptyContext(), result ->
                "Incorrect modifiers - expected: %s, actual: %s".formatted(expandModifiers(expectedModifiers), expandModifiers(result.object())));
        }

        assertEquals(classRecord.superclass, clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "", emptyContext(), result ->
            "Class %s does not have correct superclass".formatted(classRecord.identifier));

        List<String> expectedInterfaces = classRecord.interfaces;
        Set<String> actualInterfaces = Arrays.stream(clazz.getInterfaces())
            .map(Class::getName)
            .collect(Collectors.toSet());
        assertTrue(actualInterfaces.containsAll(expectedInterfaces), emptyContext(), result ->
            "Class %s does not implement all required interfaces. Missing: %s".formatted(clazz.getName(),
                expectedInterfaces.stream().filter(interfaceName -> !actualInterfaces.contains(interfaceName)).collect(Collectors.toSet())));

        List<Field> actualFields = List.of(clazz.getDeclaredFields());
        for (FieldRecord fieldRecord : classRecord.fields) {
            Field field = actualFields.stream()
                .filter(f -> f.getName().equals(fieldRecord.identifier))
                .findAny()
                .orElseThrow(() -> fail(emptyContext(), result ->
                    "Field %s does not exist in class %s".formatted(fieldRecord.identifier, classRecord.identifier)));

            int expectedModifiers = fieldRecord.modifiers
                .stream()
                .mapToInt(MODIFIERS::get)
                .sum();
            assertEquals(expectedModifiers, field.getModifiers() & expectedModifiers, emptyContext(), result ->
                "Incorrect modifiers for field %s in class %s - expected: %s, actual: %s"
                    .formatted(fieldRecord.identifier, classRecord.identifier, expandModifiers(expectedModifiers), expandModifiers(result.object())));

            assertEquals(fieldRecord.type, field.getType().getName(), emptyContext(), result ->
                "Field %s in class %s does not have the expected type".formatted(fieldRecord.identifier, classRecord.identifier));
        }

        List<Method> actualMethods = Stream.of(clazz.getDeclaredMethods())
            .filter(m -> !m.getName().startsWith("lambda$") && !m.isSynthetic())
            .toList();
        for (MethodRecord methodRecord : classRecord.methods) {
            String parameterString = String.join(", ", methodRecord.parameterTypes);
            Method method = actualMethods.stream()
                .filter(m ->
                    m.getName().equals(methodRecord.identifier) &&
                        m.getParameterCount() == methodRecord.parameterTypes.size() &&
                        Arrays.stream(m.getParameterTypes()).map(Class::getName).toList().containsAll(methodRecord.parameterTypes))
                .findAny()
                .orElseThrow(() -> fail(emptyContext(), result ->
                    "Method %s(%s) does not exist in class %s"
                        .formatted(methodRecord.identifier, parameterString, classRecord.identifier)));

            int expectedModifiers = methodRecord.modifiers
                .stream()
                .mapToInt(MODIFIERS::get)
                .sum();
            assertEquals(expectedModifiers, method.getModifiers() & expectedModifiers, emptyContext(), result ->
                "Incorrect modifiers for method %s(%s) in class %s - expected: %s, actual: %s"
                    .formatted(methodRecord.identifier, parameterString, classRecord.identifier, expandModifiers(expectedModifiers), expandModifiers(result.object())));

            assertEquals(methodRecord.returnType, method.getReturnType().getName(), emptyContext(), result ->
                "Method %s(%s) in class %s does not have the expected return type"
                    .formatted(methodRecord.identifier, parameterString, classRecord.identifier));
        }
    }

    public static JsonNode generateJson(List<String> packageNames) {
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
        ArrayNode rootNode = jsonNodeFactory.arrayNode();
        packageNames.stream()
            .flatMap(packageName ->
                new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"))))
                    .lines()
                    .filter(s -> s.endsWith(".class"))
                    .map(className -> getClass(packageName, className))
                    .filter(Objects::nonNull)
                    .map(ClassRecord::fromClass))
            .forEach(rootNode::addPOJO);

        return rootNode;
    }

    private static Class<?> getClass(String packageName, String className) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String expandModifiers(int modifiers) {
        String[] modifierNames = MODIFIERS.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .toArray(String[]::new);

        return IntStream.range(0, modifierNames.length)
            .filter(i -> (1 << i & modifiers) != 0)
            .mapToObj(i -> "\"%s\"".formatted(modifierNames[i]))
            .collect(Collectors.joining(", ", "[", "]"));
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static <T> T jsonNodeToRecord(JsonNode node, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(node.traverse(), tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record ClassRecord(
        List<String> modifiers,
        String identifier,
        String superclass,
        List<String> interfaces,
        List<FieldRecord> fields,
        List<MethodRecord> methods
    ) {
        public static ClassRecord fromClass(Class<?> clazz) {
            return new ClassRecord(
                MODIFIERS.entrySet()
                    .stream()
                    .filter(entry -> (clazz.getModifiers() & entry.getValue()) != 0)
                    .sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .toList(),
                clazz.getName(),
                clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "",
                Arrays.stream(clazz.getInterfaces())
                    .map(Class::getName)
                    .toList(),
                Arrays.stream(clazz.getDeclaredFields())
                    .map(FieldRecord::fromField)
                    .toList(),
                Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> !m.getName().startsWith("lambda$") && !m.isSynthetic())
                    .map(MethodRecord::fromMethod)
                    .toList()
            );
        }

        public static ClassRecord deserialize(JsonNode node) {
            return jsonNodeToRecord(node, ClassRecord.class);
        }
    }

    public record FieldRecord(
        List<String> modifiers,
        String identifier,
        String type
    ) {
        public static FieldRecord fromField(Field field) {
            return new FieldRecord(
                MODIFIERS.entrySet()
                    .stream()
                    .filter(entry -> (field.getModifiers() & entry.getValue()) != 0)
                    .sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .toList(),
                field.getName(),
                field.getType().getName()
            );
        }

        private static FieldRecord deserialize(JsonNode node) {
            return jsonNodeToRecord(node, FieldRecord.class);
        }
    }

    public record MethodRecord(
        List<String> modifiers,
        String identifier,
        String returnType,
        List<String> parameterTypes
    ) {
        public static MethodRecord fromMethod(Method method) {
            return new MethodRecord(
                MODIFIERS.entrySet()
                    .stream()
                    .filter(entry -> (method.getModifiers() & entry.getValue()) != 0)
                    .sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .toList(),
                method.getName(),
                method.getReturnType().getName(),
                Arrays.stream(method.getParameterTypes())
                    .map(Class::getName)
                    .toList()
            );
        }

        private static MethodRecord deserialize(JsonNode node) {
            return jsonNodeToRecord(node, MethodRecord.class);
        }
    }
}
