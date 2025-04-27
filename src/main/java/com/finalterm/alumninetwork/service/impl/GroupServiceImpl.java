package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.GroupNetwork;
import com.finalterm.alumninetwork.pojo.GroupNetworkUser;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.GroupRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveGroup(GroupNetwork groupNetwork) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userRepository.getUserByUsername(auth.getName());
        groupNetwork.setUser(user);
        this.groupRepository.saveGroup(groupNetwork);
    }

    @Override
    public List<GroupNetwork> getGroupNetworks(Map<String, String> params) {
        return this.groupRepository.getGroups(params);
    }

    @Override
    public GroupNetwork getGroupNetworkById(Integer id) {
        return this.groupRepository.getGroupById(id);
    }

    @Override
    public boolean deleteGroupNetworkById(Integer id) {
        return this.groupRepository.deleteGroup(id);
    }

    @Override
    public List<User> getUserGroupNetworksByIds(List<Integer> ids) {
        return this.groupRepository.getAllUserGroupsById(ids);
    }

    @Override
    public void saveGroupNetworkUser(List<GroupNetworkUser> groupNetworkUsers) {
        this.groupRepository.saveGroupNetworkUser(groupNetworkUsers);
    }
}
