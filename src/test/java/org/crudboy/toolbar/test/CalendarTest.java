package org.crudboy.toolbar.test;

import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.*;
import microsoft.exchange.webservices.data.search.CalendarView;
import org.crudboy.toolbar.msexchange.AppointmentRequest;
import org.crudboy.toolbar.msexchange.CalendarClient;
import org.crudboy.toolbar.msexchange.Organizer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CalendarTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private CalendarClient calendarClient;
    private List<String> itemIds;

    @Before
    public void setUp() {
        String userName = "test";
        String passWord = "test@passwd";
        calendarClient = new CalendarClient("https://0.0.0.0/EWS/Exchange.asmx", userName, passWord);
    }

    @Test
    public void AppointmentSearchTest() throws Exception {
        Date beginTime = new Date();
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTimeMax = localDate.atTime(LocalTime.MAX);
        Date endTime = Date.from(localDateTimeMax.atZone(ZoneId.systemDefault()).toInstant());
        CalendarView calendarView = new CalendarView(beginTime, endTime);
        FolderId folderId = new FolderId(WellKnownFolderName.Calendar, new Mailbox("test@test.com"));
        List<Appointment> appointmentList = calendarClient.getAppointment(folderId, calendarView);
    }


    //测试预订约会
    @Test
    public void newAppointmentTest() throws Exception {
        AppointmentRequest appointmentRequest = new AppointmentRequest();
        appointmentRequest.setSubject("5.23");
        appointmentRequest.setBody("This is a body");
        appointmentRequest.setLocation("NKG");
        appointmentRequest.setStart(DateTime.now().plusHours(1).toDate());
        appointmentRequest.setEnd(DateTime.now().plusHours(2).toDate());
        Organizer orgnizer = new Organizer();
        orgnizer.setId("peter");
        orgnizer.setAddress("perter@test.com");
        orgnizer.setName("peter");
        appointmentRequest.setOrganizer(orgnizer);
        List<Attendee> attendees = Arrays.asList(new Attendee(new EmailAddress("john@test.com")));
        appointmentRequest.setRequiredAttendees(attendees);
        List<Attendee> attendeeList = Arrays.asList(new Attendee(new EmailAddress("shaun@test.com")));
        appointmentRequest.setOptionalAttendees(attendeeList);
        calendarClient.createAppointment(appointmentRequest);
    }

    @Test
    public void cancelAppointmentTest() throws Exception {
        String appointmentId = getID();
        calendarClient.cancelAppointment(appointmentId);
    }

    @Test
    public void updateAppointmentTest() throws Exception {
        String appointmentId = getID();
        AppointmentRequest appointmentRequest = new AppointmentRequest();
        appointmentRequest.setSubject("更新后的test会议2");
        appointmentRequest.setBody("更新后的内容主体");
        appointmentRequest.setLocation("更新后的会议室位置在这");
        appointmentRequest.setStart(DateTime.now().plusHours(2).toDate());
        appointmentRequest.setEnd(DateTime.now().plusHours(3).toDate());
        Organizer orgnizer = new Organizer();
        orgnizer.setId("peter");
        orgnizer.setAddress("perter@test.com");
        orgnizer.setName("peter");
        appointmentRequest.setOrganizer(orgnizer);
        List<Attendee> attendees = Arrays.asList(new Attendee(new EmailAddress("john@test.com")));
        appointmentRequest.setRequiredAttendees(attendees);
        calendarClient.updateAppointment(appointmentId, appointmentRequest);
    }

    //测试周期预定约会
    @Test
    public void createRepeatedAppointmentTest() throws Exception {
        String period = "a";
        String limitTime = "2019-05-24 18:17:53";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dLimitTime = format.parse(limitTime);

        AppointmentRequest appointmentRequest = new AppointmentRequest();
        appointmentRequest.setSubject("this is a test2!");
        appointmentRequest.setBody("这是一个循环会议内容主体");
        appointmentRequest.setLocation("会议室位置在这");
        appointmentRequest.setStart(DateTime.now().plusHours(1).toDate());
        appointmentRequest.setEnd(DateTime.now().plusHours(2).toDate());
        appointmentRequest.setPeriod(period);
        Organizer orgnizer = new Organizer();
        orgnizer.setId("peter");
        orgnizer.setAddress("perter@test.com");
        orgnizer.setName("peter");
        appointmentRequest.setOrganizer(orgnizer);
        List<Attendee> attendees = Arrays.asList(new Attendee(new EmailAddress("john@test.com")));
        appointmentRequest.setRequiredAttendees(attendees);
        List<Attendee> attendeeList = Arrays.asList(new Attendee(new EmailAddress("shaun@test.com")));
        appointmentRequest.setOptionalAttendees(attendeeList);
        calendarClient.createRepeatedAppointment(dLimitTime, appointmentRequest);
    }

    /**
     * Get first appointment
     *
     * @return
     * @throws Exception
     */
    public String getID() throws Exception {
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTimeMax = localDate.atTime(LocalTime.MAX);
        Date beginTime = Date.from(localDate.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(localDateTimeMax.atZone(ZoneId.systemDefault()).toInstant());
        CalendarView calendarView = new CalendarView(beginTime, endTime);
        FolderId folderId = new FolderId(WellKnownFolderName.Calendar, new Mailbox("test@test.com"));
        List<Appointment> appointmentList = calendarClient.getAppointment(folderId, calendarView);
        return appointmentList.get(0).getId().getUniqueId();
    }

}

