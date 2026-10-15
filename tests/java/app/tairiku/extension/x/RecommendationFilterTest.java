package app.tairiku.extension.x;

/** Dependency-free regression checks for X recommendation entry IDs. */
public final class RecommendationFilterTest {
    public static void main(String[] args) throws Exception {
        // Post-detail Discover more: the missing case in the original patch.
        expectHidden("tweetdetailrelatedtweets-123456789");
        expectHidden("tweetdetailrelatedtweets");
        expectVisible("tweetdetail-123456789");
        expectVisible("tweetdetail-relatedposts-123456789");
        expectVisible("tweetdetail");
        // Preserve the original Search filter.
        expectHidden("toptabsrpusermodule-123456789");

        // Never drop the original post, genuine replies, or conversation containers.
        expectVisible("tweet-123456789");
        expectVisible("conversationthread-123456789");
        expectVisible("who-to-follow-123456789");
        expectVisible("cursor-bottom-0");
        expectVisible(null);

        // Dedicated score modules are filtered only by the optional fourth patch.
        expectSportsHidden("sports-score-mlb-123");
        expectSportsHidden("sports-scores-mlb-123");
        expectSportsHidden("scoreboard-mlb-123");
        expectSportsHidden("live-score-mlb-123");
        expectSportsHidden("game-score-mlb-123");
        expectSportsVisible("tweet-123456");
        expectSportsVisible("tweet-123-sports-score-mlb");
        expectSportsVisible("conversationthread-123-scoreboard-mlb");
        expectSportsVisible("tweetdetailrelatedtweets-456");
        expectSportsVisible("sports-news-mlb");
        expectSportsVisible("happening-now-mlb");
        expectSportsVisible("cursor-bottom-0");
        expectSportsVisible(null);

        // Check the model's verified entry-id accessor remains usable.
        java.lang.reflect.Method accessor = RecommendationFilter.class
                .getDeclaredMethod("entryId", Object.class);
        accessor.setAccessible(true);
        Object id = accessor.invoke(null, new TimelineItem("tweetdetail-42"));
        if (!"tweetdetail-42".equals(id)) {
            throw new AssertionError("Entry-id accessor stopped finding a public c() method");
        }
        System.out.println("RecommendationFilter: recommendation and candidate sports-score entry IDs passed");
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

    private static void expectSportsHidden(String id) {
        if (!RecommendationFilter.isSportsScoreEntryId(id)) {
            throw new AssertionError("Expected dedicated sports score module to be filtered: " + id);
        }
    }

    private static void expectSportsVisible(String id) {
        if (RecommendationFilter.isSportsScoreEntryId(id)) {
            throw new AssertionError("Non-score timeline entry would be hidden: " + id);
        }
    }

    public static final class TimelineItem {
        private final String id;

        TimelineItem(String id) { this.id = id; }
        public String c() { return id; }
    }
}
