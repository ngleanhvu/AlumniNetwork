package com.finalterm.alumninetwork.util;

public class ReactionUtil {
    public static String generatePostReactionZSetKey(int postId, String type, int page) {
        return "post:"+postId+":reactions:type:"+type+":page:"+page;
    }

    public static String generateReactionHashKey(String reactionId) {
        return "reaction:"+reactionId;
    }

    public static String generateReactionByPostIdAndUserIdKey(int postId, int userId) {
        return "post:"+postId+":reactions:user:"+userId;
    }

}
