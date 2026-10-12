package app.tairiku.extension.x;

/** Dependency-free regression checks for X recommendation entry IDs. */
public final class RecommendationFilterTest {
    public static void main(String[] args) throws Exception {
        // Post-detail Discover more: the missing case in the original patch.
        expectHidden("tweetdetail-123456789");
        expectHidden("tweetdetail-relatedposts-123456789");
        expectHidden("tweetdetail");
        // Preserve the original Search filter.
        expectHidden("toptabsrpusermodule-123456789");

        // Never drop the original post, genuine replies, or conversation containers.
        expectVisible("tweet-123456789");
        expectVisible("conversationthread-123456789");
        expectVisible("who-to-follow-123456789");
        expectVisible("cursor-bottom-0");
        expectVisible(null);

        // Check the model's verified entry-id accessor remains usable.
        java.lang.reflect.Method accessor = RecommendationFilter.class
                .getDeclaredMethod("entryId", Object.class);
        accessor.setAccessible(true);
        Object id = accessor.invoke(null, new TimelineItem("tweetdetail-42"));
        if (!"tweetdetail-42".equals(id)) {
            throw new AssertionError("Entry-id accessor stopped finding a public c() method");
        }
        System.out.println("RecommendationFilter: 10 entry-id cases and accessor passed");
    }

    private static void expectHidden(String id) {
        if (!RecommendationFilter.isFindMoreEntryId(id)) {
            throw new AssertionError("Expected Discover more entry to be filtered: " + id);
        }
    }

    private static void expectVisible(String id) {
        if (RecommendationFilter.isFindMoreEntryId(id)) {
            throw new AssertionError("Unrelated timeline entry would be hidden: " + id);
        }
    }

    public static final class TimelineItem {
        private final String id;

        TimelineItem(String id) { this.id = id; }
        public String c() { return id; }
    }
}
