package za.org.grassroot.webapp.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import za.org.grassroot.core.domain.Group;
import za.org.grassroot.core.domain.User;
import za.org.grassroot.core.domain.geo.GroupLocation;
import za.org.grassroot.core.domain.geo.PreviousPeriodUserLocation;
import za.org.grassroot.services.GroupBroker;
import za.org.grassroot.services.UserManagementService;
import za.org.grassroot.services.geo.GeoLocationBroker;
import za.org.grassroot.webapp.controller.BaseController;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by luke on 2016/05/12.
 */
@Controller
@RequestMapping("/emulator/test")
public class TestEmulatorController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(TestEmulatorController.class);

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private GroupBroker groupBroker;

    @Autowired
    private GeoLocationBroker geoLocationBroker;

    @RequestMapping(value = "/ajax/list", method = RequestMethod.GET)
    public String ajaxListMembersView(Model model) {

        Set<Group> groups = permissionBroker.getActiveGroups(getUserProfile(), null);

        model.addAttribute("groups", new ArrayList<>(groups));
        model.addAttribute("userUid", getUserProfile().getUid());

        return "emulator/ajax_list";
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @RequestMapping(value = "/geo/start", method = RequestMethod.GET)
    public String locationTestStart(Model model) {

        Set<Group> groups = permissionBroker.getActiveGroups(getUserProfile(), null);
        log.info("Found this many groups for the user: " + groups.size());
        model.addAttribute("groups", new ArrayList<>(groups));
        model.addAttribute("userUid", getUserProfile().getUid());
        return "emulator/geo_start";
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @RequestMapping(value = "/geo/user", method = RequestMethod.GET)
    public String locationUserCalculate(Model model, @RequestParam(value = "groupUid", required = false) String groupUid,
                                        @RequestParam(value="calculate", required = false) boolean calculate) {

        // todo: add something to do flexible local date
        log.info("Inside user calculation! For this groupUid = " + groupUid);

        final LocalDate today = LocalDate.now();
        if (calculate) geoLocationBroker.calculatePreviousPeriodUserLocations(today);

        List<User> usersToDisplay;
        Map<User, PreviousPeriodUserLocation> userLocationMap = new HashMap<>();

        if (groupUid != null && !groupUid.trim().equals("")) {
            // todo: need a function, "get users in graph" (for multiple reasons)
            usersToDisplay = new ArrayList<>(groupBroker.load(groupUid).getMembers());
        } else {
            usersToDisplay = geoLocationBroker.fetchUsersWithRecordedAverageLocations(today);
        }

        // this is going to be incredibly slow ... once get to using & analyzing, will need to optimize, a lot
        for (User u : usersToDisplay) {
            userLocationMap.put(u, geoLocationBroker.fetchUserLocation(u.getUid(), today));
        }

        model.addAttribute("userLocations", userLocationMap);
        return "emulator/geo_user";
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @RequestMapping(value = "/geo/group", method = RequestMethod.GET)
    public String locationGroupCalculate(Model model, @RequestParam("groupUid") String groupUid, @RequestParam boolean calculate) {
        final LocalDate today = LocalDate.now();

        if (groupUid != null && !groupUid.trim().equals("")) {
            if (calculate) groupBroker.calculateGroupLocation(groupUid, LocalDate.now());

            Group group = groupBroker.load(groupUid);
            GroupLocation groupLocation = geoLocationBroker.fetchGroupLocationWithScoreAbove(groupUid, today, 0);

            model.addAttribute("group", group);
            model.addAttribute("groupLocation", groupLocation);

            List<PreviousPeriodUserLocation> memberLocations = new ArrayList<>();
            for (User u : group.getMembers())
                memberLocations.add(geoLocationBroker.fetchUserLocation(u.getUid(), today));
            model.addAttribute("userLocations", memberLocations);

            return "emulator/geo_group_single_result";

        } else {
            List<Group> allGroups = groupBroker.loadAll();
            List<GroupLocation> allGroupLocations = new ArrayList<>();

            for (Group g : allGroups) {
                if (calculate) groupBroker.calculateGroupLocation(g.getUid(), LocalDate.now());
                allGroupLocations.add(geoLocationBroker.fetchGroupLocationWithScoreAbove(g.getUid(), today, 0));
            }

            model.addAttribute("groupLocations", allGroupLocations);

            return "emulator/geo_group_all_results";
        }
    }

}
