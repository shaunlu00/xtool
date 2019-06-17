package org.crudboy.toolbar.msexchange;

import microsoft.exchange.webservices.data.property.complex.Attendee;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AppointmentRequest {

    private String id;
    private String subject;
    private String body;
    private Date start;
    private Date end;
    private String location;
    private Organizer organizer;
    private List<Attendee> requiredAttendees = new ArrayList<>();
    private List<Attendee> optionalAttendees = new ArrayList<>();
    private String period;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Attendee> getRequiredAttendees() {
        return requiredAttendees;
    }

    public void setRequiredAttendees(List<Attendee> requiredAttendees) {
        this.requiredAttendees = requiredAttendees;
    }

    public List<Attendee> getOptionalAttendees() {
        return optionalAttendees;
    }

    public void setOptionalAttendees(List<Attendee> optionalAttendees) {
        this.optionalAttendees = optionalAttendees;
    }
}
