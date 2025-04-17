package com.finalterm.alumninetwork.formatter;

import com.finalterm.alumninetwork.pojo.GroupNetwork;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class GroupNetworkFormatter implements Formatter<GroupNetwork> {
    @Override
    public GroupNetwork parse(String text, Locale locale) throws ParseException {
        GroupNetwork groupNetwork = new GroupNetwork();
        groupNetwork.setId(Integer.parseInt(text));
        return groupNetwork;
    }

    @Override
    public String print(GroupNetwork object, Locale locale) {
        return object.toString();
    }
}
