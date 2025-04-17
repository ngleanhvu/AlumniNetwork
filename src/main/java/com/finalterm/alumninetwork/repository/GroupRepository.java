package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.GroupNetwork;
import com.finalterm.alumninetwork.pojo.User;

import java.util.List;
import java.util.Map;

public interface GroupRepository {
    void saveGroup(GroupNetwork groupNetwork);
    List<GroupNetwork> getGroups(Map<String, String> params);
    GroupNetwork getGroupById(Integer groupId);
    boolean deleteGroup(Integer groupId);
    List<User> getAllUserGroupsById(List<Integer> groupIds);
}
