package ut.ch.nine.confluence-confidentiality;

import org.junit.Test;
import ch.nine.confluence-confidentiality.api.MyPluginComponent;
import ch.nine.confluence-confidentiality.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}