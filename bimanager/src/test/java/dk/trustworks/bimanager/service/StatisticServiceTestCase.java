package dk.trustworks.bimanager.service;

import dk.trustworks.bimanager.client.RestDelegate;
import dk.trustworks.bimanager.dto.AmountPerItem;
import dk.trustworks.bimanager.dto.TaskWorkerConstraint;
import dk.trustworks.bimanager.dto.User;
import dk.trustworks.bimanager.dto.Work;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.mockito.Mockito.when;

/**
 * Created by hans on 08/07/16.
 */
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(Parameterized.class)
//@PrepareForTest({StatisticService.class})
public class StatisticServiceTestCase {

    @Mock
    private RestDelegate restDelegate;

    @InjectMocks
    private StatisticService statisticService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIt() {

        int year = 2016;
        boolean fiscal = true;

        String user1UUID = UUID.randomUUID().toString();
        User user1 = new User(user1UUID, "Hans", "", "Hans", "", "", new Date(), true, user1UUID, "ACTIVE", new Date(), 37);

        String user2UUID = UUID.randomUUID().toString();
        User user2 = new User(user2UUID, "Thomas", "", "Thomas", "", "", new Date(), true, user2UUID, "ACTIVE", new Date(), 37);

        String user3UUID = UUID.randomUUID().toString();
        User user3 = new User(user3UUID, "Peter", "", "Peter", "", "", new Date(), true, user3UUID, "ACTIVE", new Date(), 37);

        Map<String, User> userMap = new HashMap<>();
        userMap.put(user1.getUUID(), user1);
        userMap.put(user2.getUUID(), user2);
        userMap.put(user3.getUUID(), user3);
/*
        int[] user1CapacityThisYear = {160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160};
        int[] user2CapacityThisYear = {160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160};
        int[] user3CapacityThisYear = {160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160};

        int[] user1CapacityPrevYear = {160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160};
        int[] user2CapacityPrevYear = {160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160};
        int[] user3CapacityPrevYear = {160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160};
*/
        int[] user1CapacityThisYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};
        int[] user2CapacityThisYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};
        int[] user3CapacityThisYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};

        int[] user1CapacityPrevYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};
        int[] user2CapacityPrevYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};
        int[] user3CapacityPrevYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};

/*
        int[] user1CapacityThisYear = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int[] user2CapacityThisYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};
        int[] user3CapacityThisYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};

        int[] user1CapacityPrevYear = {13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
        int[] user2CapacityPrevYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};
        int[] user3CapacityPrevYear = {37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37};
*/
        String task1UUID = UUID.randomUUID().toString();

        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = new HashMap<>();
        TaskWorkerConstraint taskWorkerConstraint1 = new TaskWorkerConstraint(UUID.randomUUID().toString(), 1250, user1.getUUID(), task1UUID);
        TaskWorkerConstraint taskWorkerConstraint2 = new TaskWorkerConstraint(UUID.randomUUID().toString(), 1250, user1.getUUID(), task1UUID);
        TaskWorkerConstraint taskWorkerConstraint3 = new TaskWorkerConstraint(UUID.randomUUID().toString(), 1250, user2.getUUID(), task1UUID);
        TaskWorkerConstraint taskWorkerConstraint4 = new TaskWorkerConstraint(UUID.randomUUID().toString(), 1250, user3.getUUID(), task1UUID);
        taskWorkerConstraintMap.put(taskWorkerConstraint1.getUserUUID()+taskWorkerConstraint1.getTaskUUID(), taskWorkerConstraint1);
        taskWorkerConstraintMap.put(taskWorkerConstraint2.getUserUUID()+taskWorkerConstraint2.getTaskUUID(), taskWorkerConstraint2);
        taskWorkerConstraintMap.put(taskWorkerConstraint3.getUserUUID()+taskWorkerConstraint3.getTaskUUID(), taskWorkerConstraint3);
        taskWorkerConstraintMap.put(taskWorkerConstraint4.getUserUUID()+taskWorkerConstraint4.getTaskUUID(), taskWorkerConstraint4);

        List<Work> allWorkThisYear = new ArrayList<>();
        Work work1 = new Work(UUID.randomUUID().toString(), 15, 0, 2016, 138, user1UUID, task1UUID, new Date());
        Work work2 = new Work(UUID.randomUUID().toString(), 15, 1, 2016, 138, user1UUID, task1UUID, new Date());
        Work work3 = new Work(UUID.randomUUID().toString(), 15, 2, 2016, 138, user1UUID, task1UUID, new Date());
        Work work4 = new Work(UUID.randomUUID().toString(), 15, 3, 2016, 138, user1UUID, task1UUID, new Date());
        Work work5 = new Work(UUID.randomUUID().toString(), 15, 4, 2016, 138, user1UUID, task1UUID, new Date());
        Work work6 = new Work(UUID.randomUUID().toString(), 15, 5, 2016, 138, user1UUID, task1UUID, new Date());
        Work work35 = new Work(UUID.randomUUID().toString(), 15, 2, 2016, 10, user3UUID, task1UUID, new Date());
        Work work36 = new Work(UUID.randomUUID().toString(), 15, 3, 2016, 10, user2UUID, task1UUID, new Date());
        Work work37 = new Work(UUID.randomUUID().toString(), 15, 4, 2016, 10, user3UUID, task1UUID, new Date());
        Work work38 = new Work(UUID.randomUUID().toString(), 14, 4, 2016, 10, user3UUID, task1UUID, new Date());
        allWorkThisYear.add(work1);
        allWorkThisYear.add(work2);
        allWorkThisYear.add(work3);
        allWorkThisYear.add(work4);
        allWorkThisYear.add(work5);
        allWorkThisYear.add(work6);
        allWorkThisYear.add(work35);
        allWorkThisYear.add(work36);
        allWorkThisYear.add(work37);
        allWorkThisYear.add(work38);


        Work work61 = new Work(UUID.randomUUID().toString(), 10, 6, 2016, 138, user1UUID, task1UUID, new Date());
        //Work work62 = new Work(UUID.randomUUID().toString(), 14, 6, 2016, 138, user3UUID, task1UUID, new Date());
        //Work work63 = new Work(UUID.randomUUID().toString(), 14, 6, 2016, 10, user3UUID, task1UUID, new Date());
        //Work work64 = new Work(UUID.randomUUID().toString(), 14, 6, 2016, 10, user3UUID, task1UUID, new Date());
        allWorkThisYear.add(work61);

        List<Work> allWorkLastYear = new ArrayList<>();
        Work work7 = new Work(UUID.randomUUID().toString(), 15, 6, 2015, 138, user1UUID, task1UUID, new Date());
        Work work8 = new Work(UUID.randomUUID().toString(), 15, 7, 2015, 138, user1UUID, task1UUID, new Date());
        Work work9 = new Work(UUID.randomUUID().toString(), 15, 8, 2015, 138, user1UUID, task1UUID, new Date());
        Work work10 = new Work(UUID.randomUUID().toString(), 15, 9, 2015, 138, user1UUID, task1UUID, new Date());
        Work work11 = new Work(UUID.randomUUID().toString(), 15, 10, 2015, 138, user1UUID, task1UUID, new Date());
        Work work12 = new Work(UUID.randomUUID().toString(), 15, 11, 2015, 139, user1UUID, task1UUID, new Date());

        Work work51 = new Work(UUID.randomUUID().toString(), 15, 2, 2015, 10, user2UUID, task1UUID, new Date());
        Work work52 = new Work(UUID.randomUUID().toString(), 15, 5, 2015, 10, user3UUID, task1UUID, new Date());
        Work work53 = new Work(UUID.randomUUID().toString(), 15, 8, 2015, 10, user2UUID, task1UUID, new Date());
        Work work54 = new Work(UUID.randomUUID().toString(), 15, 11, 2015, 10, user3UUID, task1UUID, new Date());
        Work work55 = new Work(UUID.randomUUID().toString(), 15, 2, 2015, 10, user2UUID, task1UUID, new Date());
        Work work56 = new Work(UUID.randomUUID().toString(), 15, 3, 2015, 10, user3UUID, task1UUID, new Date());
        Work work57 = new Work(UUID.randomUUID().toString(), 15, 4, 2015, 10, user2UUID, task1UUID, new Date());
        Work work58 = new Work(UUID.randomUUID().toString(), 14, 4, 2015, 10, user3UUID, task1UUID, new Date());
        allWorkLastYear.add(work7);
        allWorkLastYear.add(work8);
        allWorkLastYear.add(work9);
        allWorkLastYear.add(work10);
        allWorkLastYear.add(work11);
        allWorkLastYear.add(work12);
        allWorkLastYear.add(work51);
        allWorkLastYear.add(work52);
        allWorkLastYear.add(work53);
        allWorkLastYear.add(work54);
        allWorkLastYear.add(work55);
        allWorkLastYear.add(work56);
        allWorkLastYear.add(work57);
        allWorkLastYear.add(work58);


        //restDelegate.getTaskWorkerConstraintMap(restDelegate.getAllProjects());

        when(restDelegate.getAllWork(year)).thenReturn(allWorkThisYear);
        when(restDelegate.getAllProjects()).thenReturn(null);
        when(restDelegate.getTaskWorkerConstraintMap(null)).thenReturn(taskWorkerConstraintMap);
        when(restDelegate.getAllUsersMap()).thenReturn(userMap);
        when(restDelegate.getAllWork(year-1)).thenReturn(allWorkLastYear);
        when(restDelegate.getCapacityPerMonthByYearByUser(year, user1UUID)).thenReturn(user1CapacityThisYear);
        when(restDelegate.getCapacityPerMonthByYearByUser(year, user2UUID)).thenReturn(user2CapacityThisYear);
        when(restDelegate.getCapacityPerMonthByYearByUser(year, user3UUID)).thenReturn(user3CapacityThisYear);
        when(restDelegate.getCapacityPerMonthByYearByUser(year-1, user1UUID)).thenReturn(user1CapacityPrevYear);
        when(restDelegate.getCapacityPerMonthByYearByUser(year-1, user2UUID)).thenReturn(user2CapacityPrevYear);
        when(restDelegate.getCapacityPerMonthByYearByUser(year-1, user3UUID)).thenReturn(user3CapacityPrevYear);

        List<AmountPerItem> billablehourspercentageperuser = statisticService.billablehourspercentageperuser(year, fiscal);

        for (AmountPerItem amountPerItem : billablehourspercentageperuser) {
            System.out.println("amountPerItem = " + amountPerItem);
        }


    }
}
