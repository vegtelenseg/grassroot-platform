package za.org.grassroot.core.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import za.org.grassroot.TestContextConfiguration;
import za.org.grassroot.core.GrassRootApplicationProfiles;
import za.org.grassroot.core.domain.*;
import za.org.grassroot.core.enums.EventLogType;
import za.org.grassroot.core.enums.UserMessagingPreference;

import javax.transaction.Transactional;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static za.org.grassroot.core.util.DateTimeUtil.convertToSystemTime;
import static za.org.grassroot.core.util.DateTimeUtil.getSAST;

/**
 * Created by luke on 2016/04/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestContextConfiguration.class)
@Transactional
@ActiveProfiles(GrassRootApplicationProfiles.INMEMORY)
public class MeetingRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(MeetingRepositoryTest.class);

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Test
    public void shouldFindEventsByGroupBetweenTimestamps() {

        assertThat(meetingRepository.count(), is(0L));
        User user = userRepository.save(new User("0813330000"));
        Group group1 = groupRepository.save(new Group("tg1", user));
        Group group2 = groupRepository.save(new Group("tg2", user));

        Event event1 = meetingRepository.save(new Meeting("test", Instant.now().minus(7, DAYS), user, group1, "someLoc"));
        Event event2 = meetingRepository.save(new Meeting("test2", Instant.now().minus(5*7, DAYS), user, group1, "someLoc"));
        Event event3 = voteRepository.save(new Vote("test3", Instant.now().minus(7, DAYS), user, group1));
        Event event4 = meetingRepository.save(new Meeting("test4", Instant.now().minus(7, DAYS), user, group2, "someLoc"));

        Instant now = Instant.now();
        Instant oneMonthBack = LocalDateTime.now().minusMonths(1L).toInstant(ZoneOffset.UTC);
        Instant twoMonthsBack = LocalDateTime.now().minusMonths(2L).toInstant(ZoneOffset.UTC);

        List<Meeting> test1 = meetingRepository.
                findByParentGroupAndEventStartDateTimeBetweenAndCanceledFalse(group1, oneMonthBack, now);
        List<Meeting> test2 = meetingRepository.
                findByParentGroupAndEventStartDateTimeBetweenAndCanceledFalse(group1, twoMonthsBack, oneMonthBack);
        List<Vote> test3 = voteRepository.
                findByParentGroupAndEventStartDateTimeBetweenAndCanceledFalse(group1, oneMonthBack, now);
        List<Meeting> test4 = meetingRepository.
                findByParentGroupAndEventStartDateTimeBetweenAndCanceledFalse(group2, oneMonthBack, now);
        List<Event> test5 = eventRepository.
                findByParentGroupAndEventStartDateTimeBetweenAndCanceledFalse(group1, oneMonthBack, now, new Sort(Sort.Direction.ASC, "EventStartDateTime"));

        assertNotNull(test1);
        assertEquals(test1, Collections.singletonList(event1));
        assertNotNull(test2);
        assertEquals(test2, Collections.singletonList(event2));
        assertNotNull(test3);
        assertEquals(test3, Collections.singletonList(event3));
        assertNotNull(test4);
        assertEquals(test4, Collections.singletonList(event4));
        assertNotNull(test5);
        assertEquals(test5, Arrays.asList(event1, event3));
    }

    @Test
    public void findMeetingsForResponseMessagesShouldWork() {

        log.info("Finding meeting responses ...");

        User user = userRepository.save(new User("0710001111"));
        Group group = groupRepository.save(new Group("tg1", user));
        group.addMember(user);
        groupRepository.save(group);

        Meeting mtg = new Meeting("count check", Instant.now().plus(2, DAYS), user, group, "someLoc");
        Meeting mtg2 = new Meeting("count check 2", Instant.now().plus(2, DAYS), user, group, "otherLoc");
        mtg.setRsvpRequired(true);
        mtg2.setRsvpRequired(true);

        meetingRepository.save(mtg);
        meetingRepository.save(mtg2);

        assertThat(meetingRepository.count(), is(2L));

        List<Meeting> checkFirst = meetingRepository.meetingsForResponseTotals(Instant.now(), Instant.now().minus(5, MINUTES), Instant.now());
        assertThat(checkFirst.size(), is(2));
        assertThat(checkFirst.contains(mtg), is(true));
        assertThat(checkFirst.contains(mtg2), is(true));

        EventLog eventLog = new EventLog(user, mtg, EventLogType.EventRsvpTotalMessage, "message");
        eventLogRepository.save(eventLog);
        assertThat(eventLogRepository.count(), is(1L));

        List<Meeting> checkSecond = meetingRepository.meetingsForResponseTotals(Instant.now(), Instant.now().minus(5, MINUTES), Instant.now());
        assertThat(checkSecond.size(), is(1));
        assertThat(checkSecond.contains(mtg), is(false));
        assertThat(checkSecond.contains(mtg2), is(true));
    }

    @Test
    public void shouldFindMeetingsForThankYous() {

        assertThat(meetingRepository.count(), is(0L));

        User user = userRepository.save(new User("0710001111"));
        Group group = groupRepository.save(new Group("tg2", user));
        group.addMember(user);
        groupRepository.save(group);

        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
        Instant start = convertToSystemTime(LocalDateTime.of(yesterday, LocalTime.MIN), getSAST());
        Instant end = convertToSystemTime(LocalDateTime.of(yesterday, LocalTime.MAX), getSAST());

        Meeting mtgYesterday = new Meeting("check yesterday", Instant.now().minus(1, DAYS), user, group, "someLoc");
        mtgYesterday.setRsvpRequired(true);
        Meeting mtgTwoDaysAgo = new Meeting("two days ago", Instant.now().minus(2, DAYS), user, group, "anotherLoc");
        mtgTwoDaysAgo.setRsvpRequired(true);
        Meeting mtgNow = new Meeting("check today", Instant.now(), user, group, "happening now");
        mtgNow.setRsvpRequired(true);

        meetingRepository.save(Arrays.asList(mtgYesterday, mtgTwoDaysAgo, mtgNow));
        List<Meeting> mtgs = meetingRepository.findByEventStartDateTimeBetweenAndCanceledFalseAndRsvpRequiredTrue(start, end);

        assertThat(meetingRepository.count(), is(3L));
        assertThat(mtgs.size(), is(1));
        assertThat(mtgs.contains(mtgYesterday), is(true));
        assertThat(mtgs.contains(mtgTwoDaysAgo), is(false));
        assertThat(mtgs.contains(mtgNow), is(false));
    }

}
