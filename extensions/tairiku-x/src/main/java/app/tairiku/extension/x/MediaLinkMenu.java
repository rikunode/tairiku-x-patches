package app.tairiku.extension.x;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adds Copy media URL to an X post's options menu without modifying Ahmed's
 * extension classes. An unexpected app model is left to the original menu.
 */
@SuppressWarnings("unused")
public final class MediaLinkMenu {
    private static final String ACTION_NAME = "PromotedCopyLinkTo";
    private static final Pattern MEDIA_START = Pattern.compile("(?=MediaContent(?:Video|Gif|Image)\\()");
    private static final Pattern VARIANT = Pattern.compile(
        "MediaVariant\\(url=(https://[^,\\s)]+), bitRate=(\\d+|null), contentType=([^)]+)\\)");
    private static final Pattern IMAGE = Pattern.compile("imageUrl=(https://pbs\\.twimg\\.com/[^,)\\s]+)");
    private static WeakReference<Activity> activity = new WeakReference<>(null);

    private MediaLinkMenu() {}

    // The patch writes the actual obfuscated enum/group names at patch time.
    private static String actionClass() { return ""; }
    private static String groupClass() { return ""; }

    public static void setActivity(Activity value) {
        activity = new WeakReference<>(value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<?> options(boolean shown, List<?> groups) {
        if (!shown || groups == null) return groups;
        try {
            Class<?> group = Class.forName(groupClass());
            Constructor<?> ctor = group.getDeclaredConstructor(List.class);
            ctor.setAccessible(true);
            Class<? extends Enum> action = (Class<? extends Enum>) Class.forName(actionClass());
            Object option = Enum.valueOf(action, ACTION_NAME);
            ArrayList<Object> result = new ArrayList<>(groups);
            result.add(ctor.newInstance(java.util.Collections.singletonList(option)));
            return result;
        } catch (Exception | LinkageError ignored) {
            return groups;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<?, ?> labels(boolean shown, Map<?, ?> original) {
        if (!shown) return original;
        try {
            Class<? extends Enum> action = (Class<? extends Enum>) Class.forName(actionClass());
            Object option = Enum.valueOf(action, ACTION_NAME);
            Map result = original == null ? new LinkedHashMap() : new LinkedHashMap(original);
            result.put(option, java.util.Locale.getDefault().getLanguage().equals("ja")
                ? "メディアURLをコピー" : "Copy media URL");
            return result;
        } catch (Exception | LinkageError ignored) {
            return original;
        }
    }

    public static boolean onOption(Context appContext, Object post, Object event) {
        if (event == null) return false;
        try {
            Class<?> action = Class.forName(actionClass());
            for (Field field : event.getClass().getDeclaredFields()) {
                if (field.getType() != action) continue;
                field.setAccessible(true);
                Object selected = field.get(event);
                if (!(selected instanceof Enum)
                        || !ACTION_NAME.equals(((Enum<?>) selected).name())) return false;
                Activity active = activity.get();
                Context context = active != null ? active : appContext;
                List<String> links = mediaLinks(String.valueOf(post));
                if (links.isEmpty()) {
                    Toast.makeText(context, "No media URL available", Toast.LENGTH_SHORT).show();
                } else {
                    ClipboardManager clipboard =
                        (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(ClipData.newPlainText(
                            "media_urls", String.join("\n", links)));
                        Toast.makeText(context,
                            links.size() == 1 ? "Media URL copied" : "Media URLs copied",
                            Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        } catch (Exception | LinkageError ignored) {
            // Preserve X's original selection handling on unknown versions.
        }
        return false;
    }

    /** Chooses a video's highest bit-rate MP4 and normalizes images to original size. */
    public static List<String> mediaLinks(String description) {
        LinkedHashSet<String> links = new LinkedHashSet<>();
        if (description == null) return new ArrayList<>(links);
        for (String segment : MEDIA_START.split(description)) {
            if (segment.startsWith("MediaContentVideo(") || segment.startsWith("MediaContentGif(")) {
                Matcher variants = VARIANT.matcher(segment);
                String best = null;
                long bestBitrate = -1;
                while (variants.find()) {
                    if (!variants.group(3).contains("mp4")) continue;
                    long bitrate;
                    try { bitrate = variants.group(2).equals("null") ? 0 : Long.parseLong(variants.group(2)); }
                    catch (NumberFormatException ignored) { continue; }
                    if (bitrate > bestBitrate) {
                        bestBitrate = bitrate;
                        best = variants.group(1);
                    }
                }
                if (best != null) links.add(best);
            } else if (segment.startsWith("MediaContentImage(")) {
                Matcher image = IMAGE.matcher(segment);
                if (image.find()) {
                    String url = image.group(1);
                    int query = url.indexOf('?');
                    String base = query >= 0 ? url.substring(0, query) : url;
                    links.add(base + "?name=orig");
                }
            }
        }
        return new ArrayList<>(links);
    }
}
