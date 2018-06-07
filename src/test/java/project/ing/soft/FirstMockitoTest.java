package project.ing.soft;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FirstMockitoTest {

    @Test
    public void test() {
        ArrayList<String> mockList = mock(ArrayList.class);

        mockList.add("First");
        when(mockList.get(0)).thenReturn("Mockito");
        when(mockList.get(1)).thenReturn("JCG");

        assertEquals("Mockito", mockList.get(0));
        assertEquals("JCG", mockList.get(1));
    }
}
