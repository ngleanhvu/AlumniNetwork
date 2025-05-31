<<<<<<< HEAD
package com.finalterm.alumninetwork.util;

public class PostUtil {
    public static String generatePostReactionTypeCountKey(String postId, String type) {
        return "post:"+postId+":reaction:type:"+type;
    }

    public static String generateTotalCommentCount(String postId) {
        return "post:" + postId + ":commentCount";
    }

    public static String generatePostImagesKey(String postId) {
        return "post:" + postId + ":images";
    }
    public static String generatePostReactionStatsKey(String postId) {
        return "post:"+postId+":reaction:stats";
    }

    public static String generatePostProfileKey(String userId) {
        return "profile:" + userId + ":user";
    }

    public static String generatePostKey(String postId) {
        return "post:" + postId + ":key";
    }

    public static String globalFeedKey() {
        return "feed:global";
    }
}
=======
package com.finalterm.alumninetwork.util;

public class PostUtil {
    public static String generatePostReactionStatsKey(String postId) {
        return "post:"+postId+":reaction:stats";
    }

    public static String generatePostKey(String postId) {
        return "post:"+postId+":key";
    }

    public static String generatePostCommentCountKey(String postId) {
        return "post:"+postId+":comment:count";
    }

    public static String generatePostImagesKey(String postId) {
        return "post:"+postId+":images";
    }
}
>>>>>>> e3352d1 (fix reaction service add update create post)
