package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.GroupNetwork;
import com.finalterm.alumninetwork.pojo.GroupNetworkUser;
import com.finalterm.alumninetwork.pojo.User;

import java.util.List;
import java.util.Map;

public interface GroupService {
    void saveGroup(GroupNetwork groupNetwork);
    List<GroupNetwork> getGroupNetworks(Map<String, String> params);
    GroupNetwork getGroupNetworkById(Integer id);
    boolean deleteGroupNetworkById(Integer id);
    List<User> getUserGroupNetworksByIds(List<Integer> ids);
    void saveGroupNetworkUser(List<GroupNetworkUser> groupNetworkUsers);
}
