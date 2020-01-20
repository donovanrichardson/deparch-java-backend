

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceTest {
    @Test
    public void replaceTest(){
        assertEquals("roam-transit/953", "roam-transit/953/20190912".replaceAll("^(.+)/([^/]+)$","$1"));
    }

}
