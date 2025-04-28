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
