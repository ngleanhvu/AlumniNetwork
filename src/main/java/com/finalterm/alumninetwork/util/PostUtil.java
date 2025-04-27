package com.finalterm.alumninetwork.util;

public class PostUtil {
    public static String generatePostReactionTypeCountKey(String postId, String type) {
        return "post:"+postId+":reaction:type:"+type;
    }

    public static String generatePostReactionStatsKey(String postId) {
        return "post:"+postId+":reaction:stats";
    }
}
