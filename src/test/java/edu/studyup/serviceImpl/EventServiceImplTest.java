package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date());
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		DataStorage.eventData.put(event.getEventID(), event);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEvent_WrongEventID_badCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
	
	// HW 2 Test Cases
	/*
	 * Checks that the function properly throws exception
	 * in case a string is longer than 20 characters
	 * 
	 */
	@Test
	void testUpdateEvent_Over20() {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event Poorly Too Long String");
		  });
	}
	
	@Test
	void testUpdateEvent_isMaxLen() throws StudyUpException {
		int eventID = 1;
		try {
			eventServiceImpl.updateEventName(eventID, "This String 20 chars");
		}
		catch(Exception e) {
			fail("String Length is 20, but updateEventName failed");
		}
	}
	
	/*
	 * Checks that the function properly throws exception
	 * in case a student is null
	 * 
	 */
	@Test
	void testAddStudent_WrongEventID() throws StudyUpException {
		int eventID = 2;
		Student student = null;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student, eventID);
		  });
	}
	
	/*
	 * Checks that the function properly returns a list of events
	 * in the event that an event is in the list of events
	 * 
	 */
	@Test
	void testGetActive_OneActiveEvent() {
		List<Event> active = eventServiceImpl.getActiveEvents();
		assertFalse(active.isEmpty());
	}
	
	/*
	 * Checks that the function properly returns an empty list
	 * if there are no events listed
	 * 
	 */
	@Test
	void testGetActive_NoEvents() {
		eventServiceImpl.deleteEvent(1);
		List<Event> active = eventServiceImpl.getActiveEvents();
		assertTrue(active.isEmpty());
	}
	
	/*
	 * Checks that the function properly returns an empty list
	 * if there is no events
	 * 
	 */
	@Test
	void testGetActive_NoActiveEvent() {
		eventServiceImpl.deleteEvent(1);
		Event event = new Event();
		Date eventDate = new Date();
		eventDate.setTime(15680);	// way before current date
		
		event.setEventID(2);
		event.setDate(eventDate);
		event.setName("Event 2");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		DataStorage.eventData.put(event.getEventID(), event);
		
		List<Event> active = eventServiceImpl.getActiveEvents();
		assertTrue(active.isEmpty());
	}
	
	
	/*
	 * Checks that the function properly returns one past event
	 * 
	 */
	@Test
	void testPastEvents_OnePastEvents() {
		eventServiceImpl.deleteEvent(1);
		Event event = new Event();
		Date eventDate = new Date();
		eventDate.setTime(15680);
		
		event.setEventID(2);
		event.setDate(eventDate);
		event.setName("Event 2");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		DataStorage.eventData.put(event.getEventID(), event);
		
		List<Event> past = eventServiceImpl.getPastEvents();
		Event pastE1 = past.get(0);
		
		assertEquals("Event 2", pastE1.getName());
	}
	
	/*
	 * Checks that the function properly returns empty list
	 * in the case that there are no events
	 * 
	 */
	@Test
	void testPastEvents_NoPastEvents() {
		List<Event> active = eventServiceImpl.getPastEvents();
		assertTrue(active.isEmpty());
	}
	
	/*
	 * Checks that the function properly adds a student to an event
	 * in the case that there already exists a student list in an event
	 * 
	 */
	@Test
	void testAddStudent_goodCase() throws StudyUpException {
		int eventID = 1;
		Student student = new Student();
		student.setFirstName("Mary");
		student.setLastName("Jane");
		student.setEmail("maryje@email.com");
		student.setId(2);
		eventServiceImpl.addStudentToEvent(student, eventID);
		Student checkStudent = DataStorage.eventData.get(eventID).getStudents().get(1);

		assertEquals(2, DataStorage.eventData.get(eventID).getStudents().size());
		assertEquals("Mary", checkStudent.getFirstName());
		assertEquals("Jane", checkStudent.getLastName());
		assertEquals(2, checkStudent.getId());
	}
	
	@Test
	void testAddStudent_OverLimit() throws StudyUpException {
		int eventID = 1;
		Student student = new Student();
		student.setFirstName("Mary");
		student.setLastName("Jane");
		student.setEmail("maryje@email.com");
		student.setId(2);
		eventServiceImpl.addStudentToEvent(student, eventID);
		
		Student student2 = new Student();
		student2.setFirstName("Jane");
		student2.setLastName("Doe");
		student2.setEmail("Jdoe@email.com");
		student2.setId(3);
		eventServiceImpl.addStudentToEvent(student2, eventID);
		
		if(DataStorage.eventData.get(eventID).getStudents().size() > 2)
			fail("More than 2 students at this event");
	}
	
	/*
	 * Checks that the function properly creates a student list and adds a student
	 * in the case that an event has no student list created
	 * 
	 */
	
	@Test
	void testAddStudent_emptyStudentList_goodCase() throws StudyUpException {
		//Create Event2
		Event event = new Event();
		event.setEventID(2);
		event.setDate(new Date());
		event.setName("Event 2");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		DataStorage.eventData.put(event.getEventID(), event);

		Student student = new Student();
		student.setFirstName("Mary");
		student.setLastName("Jane");
		student.setEmail("maryje@email.com");
		student.setId(2);
		
		eventServiceImpl.addStudentToEvent(student, 2);
		Student checkStudent = DataStorage.eventData.get(2).getStudents().get(0);

		assertEquals("Mary", checkStudent.getFirstName());
		assertEquals("Jane", checkStudent.getLastName());
		assertEquals(2, checkStudent.getId());
	}
	
	/*
	 * Checks that the function properly deletes an event
	 * 
	 */
	@Test
	void testDeleteEvent_deletedEventID_goodCase() throws StudyUpException {
		eventServiceImpl.deleteEvent(1);
		assertNull(DataStorage.eventData.get(0));
	}
	
}