package com.finalterm.alumninetwork.util;

public class ReactionUtil {
    public static String generatePostReactionZSetKey(String postId, String type) {
        return "post:"+ postId +":reactions:type:" + type;
    }

    public static String generateReactionHashKey(String reactionId) {
        return "reaction:"+reactionId;
    }

}
