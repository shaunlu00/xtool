package org.crudboy.toolbar.msexchange;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendInvitationsMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.*;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import org.crudboy.toolbar.exception.ToolbarRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CalendarClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String serverHost;
    private final String userName;
    private final String passWord;
    private final ExchangeService exchangeService;

    public CalendarClient(String serverHost, String userName, String passWord) {
        this.serverHost = serverHost;
        this.userName = userName;
        this.passWord = passWord;
        this.exchangeService = getExchangeService();
    }

    private ExchangeService getExchangeService() {
        CustomExchangeService service = new CustomExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(userName, passWord);
        service.setCredentials(credentials);
        URI serUrl = null;
        try {
            serUrl = new URI(serverHost);
        } catch (URISyntaxException e) {
            logger.error("create exchange client error", e);
            throw new ToolbarRuntimeException("Calendar Client -- create exchange client error", e);
        }
        service.setUrl(serUrl);
        service.setTraceEnabled(true);
        return service;
    }

    /**
     * Get appointment from exchange service
     *
     * @param folderId
     * @param calendarView
     * @return
     */
    public List<Appointment> getAppointment(FolderId folderId, CalendarView calendarView) {
        FindItemsResults<Appointment> appointments = null;
        try {
            appointments = exchangeService.findAppointments(folderId, calendarView);
        } catch (Exception e) {
            logger.error("Failed to get Calendar fomr Exchange");
            throw new ToolbarRuntimeException("Calendar Client -- Could not get Calendar from Exchange Service", e);
        }
        ArrayList<Appointment> appointmentArrayList = appointments == null ? null : appointments.getItems();
        return appointmentArrayList;
    }

    /**
     * Get appointment between a time
     *
     * @param userMail
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public List<Appointment> getAppointmentFromExchange(String userMail, Date beginTime, Date endTime) {
        CalendarView calendarView = new CalendarView(beginTime, endTime);
        FolderId folderId = new FolderId(WellKnownFolderName.Calendar, new Mailbox(userMail));
        return getAppointment(folderId, calendarView);
    }

    /**
     * Create new appointment
     *
     * @param appointmentRequest
     * @return
     */
    public String createAppointment(AppointmentRequest appointmentRequest) {
        try {
            Appointment appointment = new Appointment(exchangeService);
            appointment.setSubject(appointmentRequest.getSubject());
            appointment.setBody(new MessageBody(appointmentRequest.getBody()));
            appointment.setStart(appointmentRequest.getStart());
            appointment.setEnd(appointmentRequest.getEnd());
            appointment.setLocation(appointmentRequest.getLocation());
            for (Attendee attendee : appointmentRequest.getRequiredAttendees()) {
                appointment.getRequiredAttendees().add(attendee);
            }
//            for (Attendee attendee : appointmentRequest.getOptionalAttendees()) {
//                appointment.getOptionalAttendees().add(attendee);
//            }
            appointment.save(SendInvitationsMode.SendToAllAndSaveCopy);
            Item item = Item.bind(exchangeService, appointment.getId(), new PropertySet(ItemSchema.Subject));
            return appointment.getId().getUniqueId();
        } catch (Exception e) {
            logger.error("create Appointment exception", e);
            throw new ToolbarRuntimeException("Calendar Client - create appointment exception", e);
        }
    }

    /**
     * Update appointment
     *
     * @param id
     * @param appointmentRequest
     */
    public void updateAppointment(String id, AppointmentRequest appointmentRequest) {
        try {
            ItemId appointmentId = new ItemId();
            appointmentId.setUniqueId(id);
            Appointment appointment = Appointment.bind(exchangeService, appointmentId, new PropertySet(ItemSchema.Subject));
            appointment.setSubject(appointmentRequest.getSubject());
            appointment.setBody(new MessageBody(appointmentRequest.getBody()));
            appointment.setStart(appointmentRequest.getStart());
            appointment.setEnd(appointmentRequest.getEnd());
            appointment.setLocation(appointmentRequest.getLocation());
            for (Attendee attendee : appointmentRequest.getRequiredAttendees()) {
                appointment.getRequiredAttendees().add(attendee);
            }
//            for (Attendee attendee : appointmentRequest.getOptionalAttendees()) {
//                appointment.getOptionalAttendees().add(attendee);
//            }
            appointment.update(ConflictResolutionMode.AlwaysOverwrite);
            Item item = Item.bind(exchangeService, appointment.getId(), new PropertySet(ItemSchema.Subject));
        } catch (Exception e) {
            logger.error("update Appointment exception", e);
            throw new ToolbarRuntimeException("Calendar Client - update appointment exception", e);
        }
    }


    public void cancelAppointment(String id) {
        ItemId appointmentId = new ItemId();
        appointmentId.setUniqueId(id);
        try {
            Appointment appointment = Appointment.bind(exchangeService, appointmentId, new PropertySet(ItemSchema.Subject));
            appointment.delete(DeleteMode.MoveToDeletedItems);
        } catch (Exception e) {
            logger.error("delete appointment exception", e);
            throw new ToolbarRuntimeException("Calendar Client - delete appointment exception", e);
        }
    }


    public void createRepeatedAppointment(Date limitTime, AppointmentRequest appointmentRequest) {
        try {
            String period = appointmentRequest.getPeriod();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            ZoneId zoneId = ZoneId.systemDefault();
            //读取周期
            int bookPeriod = identifyPeriod(period);
            LocalDateTime lStart = appointmentRequest.getStart().toInstant().atZone(zoneId).toLocalDateTime();
            df.format(lStart);
            LocalDateTime lEnd = appointmentRequest.getEnd().toInstant().atZone(zoneId).toLocalDateTime();
            df.format(lEnd);
            LocalDateTime lLmitTime = limitTime.toInstant().atZone(zoneId).toLocalDateTime();
            df.format(lLmitTime);
            Appointment appointment = new Appointment(exchangeService);
            appointment.setSubject(appointmentRequest.getSubject());
            appointment.setBody(new MessageBody(appointmentRequest.getBody()));
            appointment.setStart(appointmentRequest.getStart());
            appointment.setEnd(appointmentRequest.getEnd());
            appointment.setLocation(appointmentRequest.getLocation());
            for (Attendee attendee : appointmentRequest.getRequiredAttendees()) {
                appointment.getRequiredAttendees().add(attendee);
            }
//            for (Attendee attendee : appointmentRequest.getOptionalAttendees()) {
//                appointment.getOptionalAttendees().add(attendee);
//            }
            appointment.save(SendInvitationsMode.SendToAllAndSaveCopy);
            List<Object> lists = new LinkedList<Object>();
            Item item0 = Item.bind(exchangeService, appointment.getId(), new PropertySet(ItemSchema.Subject));
            lists.add(item0.getId().getUniqueId());

            if (lLmitTime.minusMonths(3).isBefore(lStart)) {
                // 将约会信息发送给EWS
                while (lStart.plusDays(bookPeriod).isBefore(lLmitTime)) {
                    Appointment temp = new Appointment(exchangeService);
                    copyAppointment(temp, appointment);
                    lStart = lStart.plusDays(bookPeriod);
                    lEnd = lEnd.plusDays(bookPeriod);
                    Instant instant1 = lStart.atZone(zoneId).toInstant();
                    temp.setStart(Date.from(instant1));
                    Instant instant2 = lEnd.atZone(zoneId).toInstant();
                    temp.setEnd(Date.from(instant2));
                    DayOfWeek dayOfWeek = lStart.getDayOfWeek();
                    String s = lStart.toString().replace("-", "").substring(0, 8);
                    if (period.toUpperCase().equals("A")) {
                        temp.save(SendInvitationsMode.SendToAllAndSaveCopy);
                    } else if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != dayOfWeek.SUNDAY) {
                        temp.save(SendInvitationsMode.SendToAllAndSaveCopy);
                    }
                    Item item = Item.bind(exchangeService, temp.getId(), new PropertySet(ItemSchema.Subject));
                    lists.add(item.getId().getUniqueId());
                }
            } else {
                throw new ToolbarRuntimeException("不能预订三个月后会议");
            }
        } catch (Exception e) {
            throw new ToolbarRuntimeException("Calendar Client -- send period appointment exception");
        }
    }

    /**
     * 更新循环约会同步到exchange
     */
    public void updateRepeatedAppointment() {


    }

    /**
     * 取消循环约会同步到exchange
     */
    public void cancelRepeatedAppointment() {


    }

    //复制约会
    public void copyAppointment(Appointment appointment, Appointment appointment0) throws Exception {
        appointment.setSubject(appointment0.getSubject());
        appointment.setBody(appointment0.getBody());
        appointment.setStart(appointment0.getStart());
        appointment.setEnd(appointment0.getEnd());
        appointment.setLocation(appointment0.getLocation());
        for (Attendee attendee : appointment0.getRequiredAttendees()) {
            appointment.getRequiredAttendees().add(attendee);
        }
//        for (Attendee attendee : appointment0.getOptionalAttendees()) {
//            appointment.getOptionalAttendees().add(attendee);
//        }
    }

    //判别重复周期
    public int identifyPeriod(String period) {
        //读取周期
        int bookPeriod = 0;
        if (period.toUpperCase().equals("A")) {
            bookPeriod = 1;//每日重复
        } else if (period.toUpperCase().equals("B")) {
            bookPeriod = 1;//每工作日重复
        } else if (period.toUpperCase().equals("C")) {
            bookPeriod = 7;//每周重复
        } else if (period.toUpperCase().equals("D")) {
            bookPeriod = 14;//每两周重复
        }
        return bookPeriod;
    }

    //通过约定时间获得约会ID
    public String useTimeGetId(String userMail, Date beginTime, Date endTime) throws ServiceLocalException {
        FolderId folderId = new FolderId(WellKnownFolderName.Calendar, new Mailbox(userMail));
        CalendarView calendarView = new CalendarView(beginTime, endTime);
        List<Appointment> appointments = getAppointment(folderId, calendarView);
        return appointments.get(0).getId().getUniqueId();
    }

}
