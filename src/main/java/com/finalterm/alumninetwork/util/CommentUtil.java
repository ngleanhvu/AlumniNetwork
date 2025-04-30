package com.finalterm.alumninetwork.util;

public class CommentUtil {
    public static String generateTotalCommentCount(String postId) {
        return "post:" + postId + ":commentCount";
    }

    public static String generateRootComment(String postId) {
        return "post:" + postId + ":rootComments";
    }

    public static String generateChildrenComment(String postId, String parentCommentId) {
        return "post:" + postId + ":comments:" + parentCommentId + ":parentComments" ;
    }

}
