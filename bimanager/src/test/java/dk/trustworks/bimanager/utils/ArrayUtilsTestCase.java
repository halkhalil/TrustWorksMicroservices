package dk.trustworks.bimanager.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by hans on 08/07/16.
 */

public class ArrayUtilsTestCase {

    private int[] numbers = {1,3,5,7,9,11,13,15,17,19};

    @Test
    public void averageFullLengthTest() {
        Assert.assertEquals("", ArrayUtils.average(numbers, numbers.length), 10.0, 0.0);
    }

    @Test
    public void averagePartLengthTest() {
        Assert.assertEquals("", ArrayUtils.average(numbers, 8), 8, 0.0);
    }
}


/*


@RunWith(PowerMockRunner.class)
@PrepareForTest({ArrayUtils.class})


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

int[] numbers = {1,3,5,7,9,11,13,15,17,19};

        mockStatic(ArrayUtils.class);

        when(ArrayUtils.average(numbers, numbers.length)).thenReturn(10.0);

        double average = ArrayUtils.average(numbers, numbers.length);
        System.out.println("average = " + average);

        verifyStatic(times(1));
 */