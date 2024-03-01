package projekt.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Utils {

    public static final Function<Object, Integer> AS_INTEGER = o -> (Integer) o;

    private Utils() {}

    public static <K, V> Map<K, V> deserializeMap(List<Map<String, ?>> serializedMap, Function<Object, K> keyMapper, Function<Object, V> valueMapper) {
        return serializedMap == null ? null : serializedMap.stream()
            .collect(Collectors.toMap(map -> keyMapper.apply(map.get("key")), map -> valueMapper.apply(map.get("value"))));
    }

    public static <K extends Enum<K>, V> Map<K, V> deserializeEnumMap(List<Map<String, ?>> serializedMap, Class<K> enumClass, Function<Object, V> valueMapper) {
        Map<String, K> enums = Arrays.stream(enumClass.getEnumConstants())
            .collect(Collectors.toMap(Enum::name, Function.identity()));
        return deserializeMap(serializedMap, enums::get, valueMapper);
    }
}
