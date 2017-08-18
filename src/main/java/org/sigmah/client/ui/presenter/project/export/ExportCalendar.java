package org.sigmah.client.ui.presenter.project.export;

import com.google.gwt.core.client.GWT;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.core.ApplicationContext;
import org.quartz.JobDataMap;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.PersonalEventDAO;
import org.sigmah.server.dao.impl.GlobalExportHibernateDAO;
import org.sigmah.server.dao.impl.PersonalEventHibernateDAO;
import org.sigmah.server.domain.calendar.PersonalEvent;
import org.sigmah.server.handler.calendar.CalendarHandler;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.CalendarType;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.servlet.ServletOutputStream;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationException;
import org.hibernate.Session;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.I18nServerModule;
import org.sigmah.server.inject.MapperModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.server.util.PersonnalEventLoader;
import static org.sigmah.server.util.PersonnalEventLoader.personalEventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ExportCalendar
 */
public class ExportCalendar extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonnalEventLoader.class);
    @Inject
    private UserDAO userDAO;
//        @Inject
//        private PersonalEventDAO  personalEventDAO;
    //private final Injector injector;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportCalendar() {
        super();
        // TODO Auto-generated constructor stub
    }
    private String message;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // Do required initialization
        message = "Hello World";
        super.init(config);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOGGER.info("Export Sigmah Calendar is started by request from IP="+getClientIpAddr(request));
        Integer projectId;
        //int count;
        String name = "id";
        String projectIdStr = request.getParameter(name);
        if (projectIdStr == null) {
            // The request parameter 'param' was not present in the query string
            // e.g. http://hostname.com?a=b
            projectIdStr = "6470";
        } else if ("".equals(projectIdStr)) {
            // The request parameter 'param' was present in the query string but has no value
            // e.g. http://hostname.com?param=&a=b
            projectIdStr = "6470";
        }
        final Injector injector = Guice.createInjector(
                // Configuration module.
                new ConfigurationModule(),
                // Persistence module.
                new PersistenceModule(),
                // Mapper module.
                new MapperModule(),
                // I18nServer module.
                new I18nServerModule());

        injector.getInstance(PersistService.class).start();
        final ProjectDAO projectDAO = injector.getInstance(ProjectDAO.class);
        projectId = new Integer(projectIdStr);
        //count = 3;
        final Project project = projectDAO.findById(projectId);
        
       // addNewPersonalEventToSigmahDB(project, injector);
      //  getPersonalEventById(injector, "66581");
        
        try {
            final EntityManager em = injector.getProvider(EntityManager.class).get();
           // Integer calId = project.getCalendarId();

            final PersonalEventDAO personalEventDAO2 = injector.getInstance(PersonalEventDAO.class);

            Session session = em.unwrap(Session.class);
            session.enableFilter(EntityFilters.HIDE_DELETED);

            List<PersonalEvent> personalEventList = personalEventDAO2.findAll();
           // int dd = personalEventList.size();
            //List<PersonalEvent> personalEventList = personalEventDAO2.findByCriteria(calId);

            //final EntityManager em = injector.getProvider(EntityManager.class).get();
            // Fetching the events
            final TypedQuery<PersonalEvent> eventQuery
                    = em.createQuery("SELECT p FROM PersonalEvent p "
                            + "WHERE p.calendarId = :calendarId and p.dateDeleted is null ORDER BY p.startDate", PersonalEvent.class);
            eventQuery.setParameter("calendarId", project.getCalendarId());

            final List<PersonalEvent> events = eventQuery.getResultList();

            net.fortuna.ical4j.model.Calendar sigmahICalendar = createICalForExport(personalEventList);

            generateOutputToExportIcal(response, project.getFullName() , sigmahICalendar);
            
        } catch (Exception ee) {
            LOGGER.error("Get ALL events " + ee.getMessage());
            ee.printStackTrace();
        }     
         finally {
            //    if (output != null) try { output.close(); } catch (IOException ignore) {}
        }
        
//  }catch(Exception ee){
//    LOGGER.error("Get ALL events " + ee.getMessage());
//    ee.printStackTrace();
//}              

        
     /*   String message = new String("BEGIN:VCALENDAR\n"
                + "VERSION:2.0\n"
                + "PRODID:-//www.marudot.com//iCal Event Maker\n"
                + "X-WR-CALNAME:Hello Alex\n"
                + "CALSCALE:GREGORIAN\n"
                + "BEGIN:VTIMEZONE\n"
                + "TZID:Asia/Baghdad\n"
                + "TZURL:http://sigmah.com\n"
                + "X-LIC-LOCATION:Asia/Baghdad\n"
                + "BEGIN:STANDARD\n"
                + "TZOFFSETFROM:+0300\n"
                + "TZOFFSETTO:+0300\n"
                + "TZNAME:AST\n"
                + "DTSTART:19700101T000000\n"
                + "END:STANDARD\n"
                + "END:VTIMEZONE\n"
                + "BEGIN:VEVENT\n"
                + "DTSTAMP:20170809T145432Z\n"
                + "UID:20170809T145432Z-1611198693@marudot.com\n"
                + "DTSTART;TZID=\"Asia/Baghdad\":20170809T120000\n"
                + "DTEND;TZID=\"Asia/Baghdad\":20170809T142000\n"
                + "SUMMARY:Hello Alex meeting!\n"
                + "DESCRIPTION:TEST SERVLET \n"
                + "LOCATION:ROOM 404\n"
                + "END:VEVENT\n"
                + "END:VCALENDAR");
*/

     LOGGER.info("Export Sigmah Calendar is finisged");

    }

    private void generateOutputToExportIcal(HttpServletResponse response, final String projectName, net.fortuna.ical4j.model.Calendar sigmahICalendar) throws ValidationException, IOException {
        response.setContentType("calendar/ical");
        response.setHeader("Content-disposition", "attachment; filename=Sigmah_" + projectName+ "_calendar.ics");
        ServletOutputStream sos = response.getOutputStream();
        CalendarOutputter outputter = new CalendarOutputter();
        if (!sigmahICalendar.getComponents().isEmpty()) { // no calendar output if there is no component
            outputter.output(sigmahICalendar, sos);
            sos.flush();
        }
        if (sos != null) {
            try {
                sos.close();
            } catch (IOException ignore) {
            }
        }
        //        if (op != null) try { op.close(); } catch (IOException ignore) {}
    }

    private void addNewPersonalEventToSigmahDB(final Project project, final Injector injector) {
        if (project != null) {
            int count =3;
            final EntityManager em = injector.getProvider(EntityManager.class).get();
            em.getTransaction().begin();
            LOGGER.info("Creating PersonalEvent for projectId=" + project.getId() );
            try {
                final java.util.Calendar cal = count < 365 ? initCalendar(2016) : initCalendar(2015);
                for (int i = 0; i < count; i++) {
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    PersonalEvent personEvent = personalEventFactory(cal.getTime(), project.getCalendarId(), i);
                    em.merge(personEvent);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                LOGGER.error("An error occured while duplicating projects.", e);
                em.getTransaction().rollback();
            } finally {
                injector.getInstance(PersistService.class).stop();
            }
            LOGGER.info("PersonnalEventLoader ended with success");
        } else {
            LOGGER.error("Project  with id = " + project.getId() + " not found.");
            injector.getInstance(PersistService.class).stop();
            LOGGER.info("PersonnalEventLoader ended with errors");
        }
    }

    private PersonalEvent getPersonalEventById(final Injector injector, final String eventId) {
        PersonalEvent event = null;
        try {
            final PersonalEventDAO personalEventDAO = injector.getInstance(PersonalEventDAO.class);
            event = personalEventDAO.findById(new Integer(eventId));
        } catch (Exception ee) {
            LOGGER.error("Get event by ID" + ee.getMessage());
            ee.printStackTrace();
        }
        return event;
    }

    /**
     * @param personalEventList
     * @return
     */
    private net.fortuna.ical4j.model.Calendar createICalForExport(
            List<PersonalEvent> personalEventList) {
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Sigmah 2.2//iCal4j 2.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        //List<VEvent> vevents = createCalEventsForExport(personalEventList);
        calendar.getComponents().addAll(createCalEventsForExport(personalEventList));
        return calendar;
    }

    private List<VEvent> createCalEventsForExport(List<PersonalEvent> personalEventList) {
        List<VEvent> vevents = new ArrayList<VEvent>();

        for (Iterator<PersonalEvent> iterator = personalEventList.iterator(); iterator.hasNext();) {
            PersonalEvent next = iterator.next();
//                // initialise as an all-day event..
//VEvent christmas = new VEvent(new Date(calendar.getTime()), "Christmas Day");

//Creating an event
//  java.util.Calendar cal = java.util.Calendar.getInstance();
//  cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
//  cal.set(java.util.Calendar.DAY_OF_MONTH, 25);
//                VEvent christmas = new VEvent(new Date(next.getStartDate()),
//                        new Date(next.getEndDate()), next.getSummary());
//                //      new Date(cal.getTime()), "Christmas Day");
//                // initialise as an all-day event..
//                christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);
//
//                UidGenerator uidGenerator = new UidGenerator(next.getId().toString());
//                christmas.getProperties().add(uidGenerator.generateUid());
//
//                calendar.getComponents().add(christmas);
            VEvent vevent = new VEvent();

//                vevent.getProperties().add(new DtStart(new DateTime(next.getStartDate())));
//                vevent.getProperties().add(new DtEnd(new DateTime(next.getEndDate().getTime())));
//                UidGenerator uidGenerator = new UidGenerator(next.getId().toString());
//
//                vevent.getProperties().add(uidGenerator.generateUid())
            vevent.getProperties().add(new DtStart(new DateTime(next.getStartDate().getTime())));
            vevent.getProperties().add(new DtEnd(new DateTime(next.getEndDate().getTime())));
            vevent.getProperties().add(new Description(next.getDescription()));
            vevent.getProperties().add(new Uid(next.getId().toString()));
            vevent.getProperties().add(new Summary(next.getSummary()));
            vevent.getProperties().add(new Created(new DateTime(next.getDateCreated())));
            vevent.getProperties().add(new Transp("OPAQUE"));

// vevent.getProperties().add(uidGenerator.generateUid());
//vevent.getProperties().add(new DtStart(new Date(next.getStartDate().getTime())));
//                vevent.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);
//getProperties().add(new DtEnd(new Date(next.getEndDate().getTime())));
//                vevent.getProperties().getProperty(Property.DTEND).getParameters().add(Value.DATE);
            vevents.add(vevent);
        }
        return vevents;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * init calendar.
     *
     * @param year
     * @return
     */
    private static java.util.Calendar initCalendar(int year) {
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR, year);
        cal.set(java.util.Calendar.MONTH, 0);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        return cal;
    }
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_VIA");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("REMOTE_ADDR");
        } else if (ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
