package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.GroupNetwork;
import com.finalterm.alumninetwork.pojo.GroupNetworkUser;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.GroupRepository;
import com.finalterm.alumninetwork.service.GroupService;
import com.finalterm.alumninetwork.service.UserService;
import jakarta.validation.Valid;
import org.hibernate.validator.internal.engine.groups.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    @GetMapping("/groups/admin")
    public String manageGroups(Model model,
                               @ModelAttribute("groupName") String groupName) {
        Map<String, String> params = new HashMap<>();
        params.put("groupName", groupName);
        model.addAttribute("groups", this.groupService.getGroupNetworks(params));
        return "groups";
    }

    @GetMapping("/groups/admin/save")
    public String saveGroupPage(Model model) {
        model.addAttribute("group", new GroupNetwork());
        return "groups-form";
    }

    @PostMapping("/groups/admin/save")
    public String saveGroup(@Valid @ModelAttribute("group") GroupNetwork groupNetwork,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "groups-form";
        }
        this.groupService.saveGroup(groupNetwork);
        return "redirect:/groups/admin";
    }

    @PostMapping("/groups/admin/delete")
    public String deleteGroup(@ModelAttribute("group") GroupNetwork groupNetwork) {
        this.groupService.deleteGroupNetworkById(groupNetwork.getId());
        return "redirect:/groups/admin";
    }

    @GetMapping("/groups/admin/add-user/{id}")
    public String addUserToGroupPage(@PathVariable("id") Integer groupNetworkId,
                                     Model model) {
        GroupNetwork groupNetwork = this.groupService.getGroupNetworkById(groupNetworkId);
        List<Integer> userIds = groupNetwork.getGroupNetworkUsers().stream()
                        .map(gnu -> gnu.getUser().getId())
                        .toList();
        model.addAttribute("group", groupNetwork);
        model.addAttribute("users", this.userService.getAllUserExactAdmin());
        model.addAttribute("existingUserIds", userIds);
        return "groups-add-user";
    }

    @PostMapping("/groups/admin/add-user")
    public String addUserToGroup(@ModelAttribute("group") GroupNetwork groupNetwork,
                                 @ModelAttribute("userIds") List<Integer> userIds) {
        List<User> users = this.userService.getUserByIds(userIds);
        for (User user : users) {
           Predicate<User> isUserInGroup = u -> user.getGroupNetworkUsers().stream()
                    .anyMatch(userNetwork -> userNetwork.getId().equals(groupNetwork.getId()));
           if (!isUserInGroup.test(user)) {
                GroupNetworkUser groupNetworkUser = new GroupNetworkUser();
                groupNetworkUser.setUser(user);
                groupNetworkUser.setGroupNetwork(groupNetwork);
                groupNetwork.getGroupNetworkUsers().add(groupNetworkUser);
           }
        }
        this.groupService.saveGroup(groupNetwork);
        return "redirect:/groups/admin";
    }



}
