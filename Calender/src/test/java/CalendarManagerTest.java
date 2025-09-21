import model.CalendarManager;
import org.junit.Before;
import org.junit.Test;
import java.time.ZoneId;
import static org.junit.Assert.*;

public class CalendarManagerTest {
  private CalendarManager manager;

  @Before
  public void setUp() {
    manager = new CalendarManager();
  }

  @Test
  public void testDefaultCalendar() {
    assertNotNull(manager.getCurrentCalendar());
    assertEquals("default", manager.getCurrentCalendar().getName());
  }

  @Test
  public void testCreateCalendar() {
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
    assertNotNull(manager.getCalendar("Work"));
    assertEquals("Europe/London", manager.getCalendar("Work").getTimezone().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateDuplicateCalendar() {
    manager.createCalendar("default", ZoneId.of("America/Los_Angeles"));
  }

  @Test
  public void testRenameCalendar() {
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
    manager.renameCalendar("Work", "Personal");
    assertNull(manager.getCalendar("Work"));
    assertNotNull(manager.getCalendar("Personal"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRenameNonExistentCalendar() {
    manager.renameCalendar("NonExistent", "NewName");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRenameToExistingName() {
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
    manager.renameCalendar("Work", "default");
  }

  @Test
  public void testDeleteCalendar() {
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
    manager.deleteCalendar("Work");
    assertNull(manager.getCalendar("Work"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteLastCalendar() {
    manager.deleteCalendar("default");
  }

  @Test
  public void testSetCurrentCalendar() {
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
    manager.setCurrentCalendar("Work");
    assertEquals("Work", manager.getCurrentCalendar().getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNonExistentCalendar() {
    manager.setCurrentCalendar("NonExistent");
  }
}