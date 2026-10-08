package app.tairiku.extension.x;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filters X recommendation timeline modules by their server entry-id prefix.
 * Kept in a unique package so it can coexist with Ahmed Yarub's extension classes.
 */
@SuppressWarnings("unused")
public final class RecommendationFilter {
    private static final String WHO_TO_FOLLOW_PREFIX = "who-to-follow";
    private static final String FIND_MORE_PREFIX = "toptabsrpusermodule";

    /**
     * X 12.31 and 12.32 timeline items implement an interface whose public c() method returns the
     * server entry id. Cache that verified accessor per concrete model class instead of inspecting
     * every String field, so unrelated text can never be mistaken for an entry id.
     */
    private static final Map<Class<?>, Optional<Method>> ENTRY_ID_METHODS = new ConcurrentHashMap<>();

    private RecommendationFilter() {
    }

    /** Rewritten to true by the Hide Who to follow patch. */
    private static boolean hideWhoToFollow() {
        return false;
    }

    /** Rewritten to true by the Hide Find more patch. */
    private static boolean hideFindMore() {
        return false;
    }

    private static final class Setup {
        static final boolean HIDE_WHO_TO_FOLLOW = hideWhoToFollow();
        static final boolean HIDE_FIND_MORE = hideFindMore();
    }

    public static boolean hide(Object item) {
        if (item == null) return false;

        String entryId = entryId(item);
        if (entryId == null) return false;

        return (Setup.HIDE_WHO_TO_FOLLOW && entryId.startsWith(WHO_TO_FOLLOW_PREFIX))
                || (Setup.HIDE_FIND_MORE && entryId.startsWith(FIND_MORE_PREFIX));
    }

    public static Object filter(Object item) {
        return hide(item) ? null : item;
    }

    private static String entryId(Object item) {
        try {
            Method method =
                    ENTRY_ID_METHODS.computeIfAbsent(
                                    item.getClass(),
                                    type -> Optional.ofNullable(entryIdMethod(type)))
                            .orElse(null);
            if (method == null) return null;

            Object value = method.invoke(item);
            return value instanceof String ? (String) value : null;
        } catch (Exception ignored) {
            // Fail open: an unexpected model should remain visible rather than breaking the timeline.
            return null;
        }
    }

    private static Method entryIdMethod(Class<?> itemClass) {
        try {
            Method method = itemClass.getMethod("c");
            return method.getParameterCount() == 0 && method.getReturnType() == String.class ? method : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
